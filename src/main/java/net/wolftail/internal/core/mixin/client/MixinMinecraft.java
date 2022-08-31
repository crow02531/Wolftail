package net.wolftail.internal.core.mixin.client;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.network.NetworkManager;
import net.minecraft.profiler.Profiler;
import net.minecraft.profiler.Snooper;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.Timer;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.wolftail.internal.core.ExtCoreMinecraft;
import net.wolftail.internal.core.ImplPC;
import net.wolftail.internal.core.ImplPCC;
import net.wolftail.internal.core.ImplUPT;
import net.wolftail.internal.core.SectionHandler;
import net.wolftail.internal.core.network.NptClientPacketListener;
import net.wolftail.internal.core.network.NptPacketListener;

//SH: on_client_playing_change, finish_loading
//inject play context; intercept game loop for non player type
@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements ExtCoreMinecraft {
	
	@Final
	@Shadow
	public static Logger LOGGER;
	
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
	public Timer timer;
	
	@Final
	@Shadow
	public Queue<FutureTask<?>> scheduledTasks;
	
	@Final
	@Shadow
	public Snooper usageSnooper;
	
	@Shadow
	public static int debugFPS;
	
	@Shadow
	public String debug;
	
	@Shadow
	public int fpsCounter;
	
	@Shadow
	public long debugUpdateTime;
	
	@Shadow
	public long startNanoTime;
	
	@Shadow
	public Framebuffer framebufferMc;
	
	@Shadow
	public GameSettings gameSettings;
	
	@Shadow
	public int displayWidth;
	
	@Shadow
	public int displayHeight;
	
	@Shadow
	public TextureManager renderEngine;
	
	@Shadow
	public SoundHandler mcSoundHandler;
	
	@Shadow
	public MusicTicker mcMusicTicker;
	
	@Shadow
	public abstract void shutdown();
	
	@Shadow
	public abstract void updateDisplay();
	
	@Shadow
	public abstract void displayGuiScreen(GuiScreen screen);
	
	@Shadow
	public abstract void checkGLError(String message);
	
	// ---------------------------------SHADOW END---------------------------------
	
	@Unique
	private static final Logger logger = LogManager.getLogger("Wolftail/User");
	
	@Unique
	private FutureTask<Void> loadContextTask;
	
	@Unique
	private ImplPCC playContext;
	
	@Inject(method = "init", at = @At("RETURN"))
	private void on_init_return(CallbackInfo ci) {
		SectionHandler.finish_loading(false);
	}
	
	@Inject(method = "runTick", at = @At(value = "INVOKE_STRING", target = "net.minecraft.profiler.Profiler.endStartSection(Ljava/lang/String;)V", args = {
			"ldc=textures" }))
	private void on_runTick_invoke_endStartSection_0(CallbackInfo ci) {
		ImplPCC context = this.playContext;
		
		if (context != null && !context.isConnected())
			this.unloadContext(); // steve quit game
	}
	
	@Inject(method = "run", at = @At(value = "INVOKE", target = "net.minecraft.client.Minecraft.displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"))
	private void on_run_invoke_displayGuiScreen(CallbackInfo ci) {
		this.unloadContext();
	}
	
	@Inject(method = "runGameLoop", at = @At("HEAD"), cancellable = true)
	private void on_runGameLoop_head(CallbackInfo ci) {
		FutureTask<Void> task = this.loadContextTask;
		if (task != null) {
			task.run();
			
			this.loadContextTask = null;
		}
		
		ImplPCC context = this.playContext;
		
		if (context != null && !context.playType().isSteve()) {
			ci.cancel();
			
			this.doGameLoop(context);
		}
	}
	
	@Unique
	private void doGameLoop(ImplPCC context) {
		Profiler profiler = this.mcProfiler;
		profiler.startSection("root");
		
		// check close
		if (Display.isCreated() && Display.isCloseRequested())
			this.shutdown();
		
		// update timer
		this.timer.updateTimer();
		int i = Math.min(10, this.timer.elapsedTicks);
		
		profiler.startSection("scheduledExecutables");
		runQueuedTasks(this.scheduledTasks); // here we will apply all packets received
		profiler.endSection();
		
		boolean lostContext = false;
		
		// ---------------------------------Tick Start---------------------------------
		profiler.startSection("tick");
		
		// ticking part, notice that the code below dosen't always run every game loop
		for (; i-- != 0;) {
			NetworkManager connect = context.getConnection();
			
			if (connect.isChannelOpen())
				connect.processReceivedPackets();
			
			if (!connect.isChannelOpen()) {
				connect.checkDisconnected();
				
				context.playType().callClientLeave();
				this.unloadContext(); // non player type quit game
				this.displayQuitScreen(connect.getExitMessage(), connect.isLocalChannel());
				
				lostContext = true;
				break;
			}
			
			this.renderEngine.tick();
			this.mcMusicTicker.update();
			this.mcSoundHandler.update();
		}
		
		profiler.endSection();
		// ----------------------------------Tick End----------------------------------
		
		this.checkGLError("Pre render");
		
		// --------------------------------Render Start--------------------------------
		profiler.startSection("render");
		
		this.framebufferMc.bindFramebuffer(true); // bind framebuffer & set viewport
		
		// do frame
		if (!lostContext)
			context.playType().callClientFrame();
		
		this.framebufferMc.unbindFramebuffer(); // unbind framebuffer
		this.framebufferMc.framebufferRender(this.displayWidth, this.displayHeight); // blit to screen
		
		profiler.endSection();
		// ---------------------------------Render End---------------------------------
		
		this.updateDisplay();
		Thread.yield();
		
		this.checkGLError("Post render");
		
		this.fpsCounter++; // call this every game loop
		this.updateFrameTimer();
		
		while (Minecraft.getSystemTime() >= this.debugUpdateTime + 1000L) {
			debugFPS = this.fpsCounter;
			this.debug = String.format("Wolftail working with %s, %d fps", context.playType().getRegistryName(),
					debugFPS);
			
			this.debugUpdateTime += 1000L;
			this.fpsCounter = 0; // when 1s has passed, clear it
			
			this.usageSnooper.addMemoryStatsToSnooper();
			this.usageSnooper.startSnooper(); // this will check isSnooperRunning()Z itself
		}
		
		if ((i = this.gameSettings.limitFramerate) < GameSettings.Options.FRAMERATE_LIMIT.getValueMax()) {
			profiler.startSection("fpslimit_wait");
			Display.sync(i);
			profiler.endSection();
		}
		
		profiler.endSection(); // root
	}
	
	@Unique
	private void updateFrameTimer() {
		long k = System.nanoTime();
		this.frameTimer.addFrame(k - this.startNanoTime);
		this.startNanoTime = k;
	}
	
	@Unique
	private void displayQuitScreen(ITextComponent reason, boolean isLocal) {
		GuiMainMenu main = new GuiMainMenu();
		GuiScreen s = isLocal ? main : new GuiMultiplayer(main);
		
		this.displayGuiScreen(reason == null ? s : new GuiDisconnected(s, "disconnect.lost", reason));
	}
	
	@Unique
	private static void runQueuedTasks(Queue<FutureTask<?>> the_queue) {
		synchronized (the_queue) {
			while (!the_queue.isEmpty())
				Util.runTask(the_queue.poll(), LOGGER);
		}
	}
	
	@Unique
	private void unloadContext() {
		SectionHandler.on_client_playing_change();
		
		this.playContext = null;
	}
	
	@Override
	public void wolftail_loadContext(ImplUPT type, UUID id, String name, NetworkManager connect) {
		try {
			(this.loadContextTask = new FutureTask<Void>(() -> {
				ImplPCC context = playContext = new ImplPCC(type, id, name, connect);
				SectionHandler.on_client_playing_change();
				
				logger.info("The universal player type in use is {}", type.getRegistryName());
				
				if (!type.isSteve()) {
					// clean up
					currentScreen.onGuiClosed();
					currentScreen = null;
					FMLClientHandler.instance().setPlayClient(null);
					NptPacketListener.cleanFML(connect);
					connect.setNetHandler(new NptClientPacketListener(connect));
					
					type.callClientEnter(context);
				}
			}, null)).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public ImplPC wolftail_currentContext() {
		return this.playContext;
	}
}
