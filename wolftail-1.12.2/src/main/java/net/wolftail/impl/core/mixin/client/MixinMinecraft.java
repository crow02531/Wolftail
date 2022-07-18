package net.wolftail.impl.core.mixin.client;

import java.io.IOException;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.Throwables;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.network.NetworkManager;
import net.minecraft.profiler.Profiler;
import net.minecraft.profiler.Snooper;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextComponentTranslation;
import net.wolftail.api.UniversalPlayerType;
import net.wolftail.impl.core.ExtCoreMinecraft;
import net.wolftail.impl.core.ExtCoreNetworkManager;
import net.wolftail.impl.core.ImplPC;
import net.wolftail.impl.core.ImplUPT;
import net.wolftail.impl.core.SectionHandler;
import net.wolftail.impl.core.network.NoopNetHandler;

//SectionHandler.on_client_playing_change, finish_loading; new game loop
@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements ExtCoreMinecraft {
	
	//---------------------------------SHADOW START---------------------------------
	
	@Final
	@Shadow
	private static Logger LOGGER;
	
	@Final
	@Shadow
	public Profiler mcProfiler;
	
	@Final
	@Shadow
	public FrameTimer frameTimer;
	
	@Shadow
	public GuiScreen currentScreen;
	
	@Final
	@Shadow
	private Timer timer;
	
	@Final
	@Shadow
	private Queue<FutureTask<?>> scheduledTasks;
	
	@Final
	@Shadow
	private Snooper usageSnooper;
	
	@Shadow
	private NetworkManager myNetworkManager;
	
	@Shadow
	static int debugFPS;
	
	@Shadow
	public String debug;
	
	@Shadow
	private int fpsCounter;
	
	@Shadow
	private long debugUpdateTime;
	
	@Shadow
	long startNanoTime;
	
	@Final
	@Shadow
	private Session session;
	
	@Shadow
	private Framebuffer framebufferMc;
	
	@Shadow
	public int displayWidth;
	
	@Shadow
	public int displayHeight;
	
	@Shadow
	public abstract void shutdown();
	
	@Shadow
	public abstract void updateDisplay();
	
	@Shadow
	public abstract int getLimitFramerate();
	
	@Shadow
	public abstract boolean isFramerateLimitBelowMax();
	
	@Shadow
	public abstract void displayGuiScreen(GuiScreen screen);
	
	@Shadow
	private void checkGLError(String message) {}
	
	//---------------------------------SHADOW END---------------------------------
	
	@Unique
	private static final Logger logger = LogManager.getLogger("wolftail/user");
	
	@Unique
	private ImplPC.Client play_context;
	
	@Unique
	private FutureTask<Void> specialTask;
	
	@Inject(method = "init", at = @At("RETURN"))
	private void onInit(CallbackInfo info) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		SectionHandler.finish_loading(false);
	}
	
	@Inject(method = "runTick", at = @At(value = "INVOKE_STRING", target = "endStartSection(Ljava/lang/String;)V", args = { "ldc=textures" }))
	private void onRunTick(CallbackInfo info) {
		ImplPC.Client context = this.play_context;
		
		if(context != null && !context.getConnection().isChannelOpen())
			this.unloadPlayContext();
	}
	
	@Inject(method = "runGameLoop", at = @At("HEAD"), cancellable = true)
	private void onRunGameLoop(CallbackInfo info) throws LWJGLException, IOException {
		FutureTask<Void> task = this.specialTask;
		if(task != null) {
			task.run();
			
			this.specialTask = null;
		}
		
		ImplPC.Client context = this.play_context;
		ImplUPT type;
		
		if(context != null && (type = (ImplUPT) context.playType()) != UniversalPlayerType.TYPE_PLAYER) {
			info.cancel();
			
			try {
				this.doGameLoop(context, type);
			} catch(OutOfMemoryError e) {
				this.unloadPlayContext();
				
				throw e;
			}
		}
	}
	
	@Unique
	private void doGameLoop(ImplPC.Client context, ImplUPT type) throws LWJGLException, IOException {
		Profiler profiler = this.mcProfiler;
		
		profiler.startSection("root");
		
		if(Display.isCreated() && Display.isCloseRequested())
			this.shutdown();
		this.timer.updateTimer();
		
		profiler.startSection("scheduledExecutables");
		runQueuedTasks(this.scheduledTasks); //here we will apply all packets received
		profiler.endSection(); //scheduledExecutables
		
		//---------------------------------------start our section-----------------------------
		profiler.startSection("wolftailSection");
		
		boolean lostContext = false;
		
		//do tick
		profiler.startSection("tick");
		
		//ticking part, notice that the code below dosen't always run every game loop
		for(int i = 0, l = Math.min(10, this.timer.elapsedTicks); i < l; ++i) {
			NetworkManager connection = context.getConnection();
			
			if(connection.isChannelOpen())
				connection.processReceivedPackets();
			
			//re-check connection state
			if(!connection.isChannelOpen()) {
				connection.checkDisconnected();
				
				this.unloadPlayContext();
				this.myNetworkManager = null;
				this.displayGuiScreen(connection.isLocalChannel() ? new GuiMainMenu() : new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.lost", new TextComponentTranslation("disconnect.genericReason")));
				lostContext = true;
				
				break;
			}
		}
		
		profiler.endSection(); //tick
		
		//do pre frame
		profiler.startSection("frame");
		this.checkGLError("Pre render");
		this.framebufferMc.bindFramebuffer(true); //so that the results of render operations in the custom part would fall here
		
		//call custom frame
		if(!lostContext)
			type.callClientFrame(context);
		else {
			GlStateManager.clearColor(0, 0, 0, 0);
			GlStateManager.clearDepth(1);
			GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		}
		
		//do post frame
		this.framebufferMc.unbindFramebuffer(); //bind to the default
		this.framebufferMc.framebufferRender(this.displayWidth, this.displayHeight); //transport our color buffer to the default's
		this.updateDisplay();
		Thread.yield();
		this.checkGLError("Post render");
		profiler.endSection(); //frame
		
		profiler.endSection(); //wolftailSection
		//---------------------------------------end our section-------------------------------
		
		this.fpsCounter++; //call this every game loop
		this.updateFrameTimer();
		
		while(Minecraft.getSystemTime() >= this.debugUpdateTime + 1000L) {
			debugFPS = this.fpsCounter;
			this.debug = String.format("Wolftail working with %s, %d fps", context.playType().registeringId(), debugFPS);
			
			this.debugUpdateTime += 1000L;
			this.fpsCounter = 0; //when 1s has passed, clear it
			
			this.usageSnooper.addMemoryStatsToSnooper();
			this.usageSnooper.startSnooper(); //this will check isSnooperRunning()Z itself
		}
		
		if(this.isFramerateLimitBelowMax()) {
			profiler.startSection("fpslimit_wait");
			Display.sync(this.getLimitFramerate());
			profiler.endSection(); //fpslimit_wait
		}
		
		profiler.endSection(); //root
	}
	
	@Unique
	private void updateFrameTimer() {
		long k = System.nanoTime();
		this.frameTimer.addFrame(k - this.startNanoTime);
		this.startNanoTime = k;
	}
	
	@Unique
	private static void runQueuedTasks(Queue<FutureTask<?>> the_queue) {
		synchronized(the_queue) {
			while(!the_queue.isEmpty()) Util.runTask(the_queue.poll(), LOGGER);
		}
	}
	
	@Override
	public void wolftail_loginSuccess(ImplUPT type, UUID id, NetworkManager connect) {
		try {
			(this.specialTask = new FutureTask<Void>(() -> {
				ImplPC.Client context = this.play_context = new ImplPC.Client(type, id, this.session.getUsername(), connect);
				((ExtCoreNetworkManager) connect).wolftail_setPlayContext(context);
				
				SectionHandler.on_client_playing_change();
				
				logger.info("The universal player type in use is {}", type.registeringId());
				
				if(type != UniversalPlayerType.TYPE_PLAYER) {
					GuiScreen gs = this.currentScreen;
					
					if(gs != null) {
						gs.onGuiClosed();
						
						this.currentScreen = null;
					}
					
					connect.setNetHandler(new NoopNetHandler());
					type.callClientEnter(context);
				}
				
				return null;
			})).get();
		} catch(InterruptedException | ExecutionException e) {
			Throwables.rethrow(e);
		}
	}
	
	@Unique
	private void unloadPlayContext() {
		SectionHandler.on_client_playing_change();
		
		this.play_context = null;
	}
}
