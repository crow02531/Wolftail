package net.wolftail.impl.core.mixin.client;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.Timer;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.wolftail.impl.core.ExtCoreMinecraft;
import net.wolftail.impl.core.ImplPCC;
import net.wolftail.impl.core.ImplUPT;
import net.wolftail.impl.core.SectionHandler;
import net.wolftail.impl.core.network.NptClientPacketListener;

//SH: on_client_playing_change
//inject play context; intercept game loop for non player type
@Mixin(Minecraft.class)
public abstract class MixinMinecraft extends ReentrantBlockableEventLoop<Runnable> implements ExtCoreMinecraft {

	@Shadow
	public Screen screen;
	
	@Final
	@Shadow
	public Window window;
	
	@Final
	@Shadow
	public Timer timer;
	
	@Final
	@Shadow
	public FrameTimer frameTimer;
	
	@Final
	@Shadow
	public Options options;
	
	@Final
	@Shadow
	public RenderTarget mainRenderTarget;
	
	@Final
	@Shadow
	public SoundManager soundManager;
	
	@Final
	@Shadow
	public MusicManager musicManager;
	
	@Final
	@Shadow
	public TextureManager textureManager;
	
	@Shadow
	public long lastNanoTime;
	
	@Shadow
	public long lastTime;
	
	@Shadow
	public int frames;
	
	@Shadow
	public static int fps;
	
	@Shadow
	public String fpsString;
	
	@Shadow
	public ProfilerFiller profiler;
	
	@Shadow
	public ProfileResults fpsPieResults;
	
	@Shadow
	public abstract void stop();
	
	@Shadow
	public abstract void renderFpsMeter(PoseStack poseStack, ProfileResults profileResults);
	
	@Shadow
	public abstract int getFramerateLimit();
	
	@Shadow
	public abstract void setScreen(Screen screen);
	
	//---------------------------------SHADOW END---------------------------------
	
	@Unique
	private static final Logger logger = LogManager.getLogger("Wolftail/User");
	
	@Unique
	private FutureTask<Void> loadContextTask;
	
	@Unique
	private ImplPCC playContext;
	
	//unused
	protected MixinMinecraft(String string) { super(string); }
	
	@Inject(method = "tick", at = @At(value = "INVOKE_STRING", target = "popPush(Ljava/lang/String;)V", args = { "ldc=textures" }))
	private void on_tick_invoke_popPush_0(CallbackInfo info) {
		ImplPCC context = this.playContext;
		
		if(context != null && !context.isConnected())
			this.unloadContext(); //steve quit game
	}
	
	@Inject(method = "run", at = @At(value = "INVOKE", target = "emergencySave()V", ordinal = 0))
	private void on_run_invoke_emergencySave_0(CallbackInfo ci) {
		this.unloadContext();
	}
	
	@Inject(method = "runTick", at = @At("HEAD"), cancellable = true)
	private void on_runTick_head(CallbackInfo ci) {
		FutureTask<Void> task = this.loadContextTask;
		if(task != null) {
			task.run();
			
			this.loadContextTask = null;
		}
		
		ImplPCC context = this.playContext;
		
		if(context != null && !context.playType().isPlayerType()) {
			ci.cancel();
			
			this.doGameLoop(context);
		}
	}
	
	@Unique
	private void doGameLoop(ImplPCC context) {
		this.window.setErrorSection("Pre render");
		
		//check close
		if(this.window.shouldClose())
			this.stop();
		
		//update timer
		int i = Math.min(10, this.timer.advanceTime(Util.getMillis()));
		
		//run scheduled executables
		this.profiler.push("scheduledExecutables");
		this.runAllTasks();
		this.profiler.pop();
		
		boolean lostContext = false;
		
		//---------------------------------Tick Start---------------------------------
		this.profiler.push("tick");
		
		//ticking part, notice that the code below dosen't always run every game loop
		for(; i-- != 0;) {
			this.profiler.incrementCounter("clientTick");
			
			Connection connect = context.getConnection();
			
			if(!lostContext) {
				this.profiler.push("gameMode");
				connect.tick();
				this.profiler.pop();
				
				if(!connect.isConnected()) {
					lostContext = true;
					
					context.playType().callClientLeave();
					this.unloadContext(); //non player type quit game
					this.setQuitScreen(connect.getDisconnectedReason(), connect.isMemoryConnection());
				}
			}
			
			this.profiler.push("textures");
			this.textureManager.tick();
			this.profiler.pop();
			
			this.musicManager.tick();
			this.soundManager.tick(false);
		}
		
		this.profiler.pop();
		//----------------------------------Tick End----------------------------------
		
		this.profiler.push("sound");
		this.soundManager.updateSource(null);
		this.profiler.pop();
		
		this.window.setErrorSection("Render");
		
		//--------------------------------Render Start--------------------------------
		this.profiler.push("render");
		
		PoseStack poseStack = RenderSystem.getModelViewStack();
		poseStack.pushPose();
		RenderSystem.applyModelViewMatrix();
		
		FogRenderer.setupNoFog();
		RenderSystem.enableTexture();
		RenderSystem.enableCull();
		
		this.mainRenderTarget.bindWrite(true); //bind framebuffer & set viewport
		
		//call custom frame
		if(!lostContext) {
			context.playType().callClientRender();
		} else {
			RenderSystem.clearColor(0, 0, 0, 0);
			RenderSystem.clearDepth(1);
			RenderSystem.clear(GlConst.GL_COLOR_BUFFER_BIT | GlConst.GL_DEPTH_BUFFER_BIT, false);
		}
		
		//draw fps pie
		if(this.fpsPieResults != null)
			this.renderFpsMeter(new PoseStack(), this.fpsPieResults);
		
		this.mainRenderTarget.unbindWrite(); //unbind framebuffer
		
		poseStack.popPose();
		
		poseStack.pushPose();
		RenderSystem.applyModelViewMatrix();
		this.mainRenderTarget.blitToScreen(this.window.getWidth(), this.window.getHeight());
		poseStack.popPose();
		
		this.profiler.pop();
		//---------------------------------Render End---------------------------------
		
		this.window.updateDisplay();
		if((i = this.getFramerateLimit()) < Option.FRAMERATE_LIMIT.getMaxValue())
			RenderSystem.limitDisplayFPS(i);
		Thread.yield();
		
		this.window.setErrorSection("Post render");
		
		this.frames++;
		this.updateFrameTimer();
		
		while(Util.getMillis() >= this.lastTime + 1000L) {
			fps = this.frames;
			
			this.fpsString = String.format("Wolftail working with %s, %d fps", context.playType().registeringId(), fps);
			this.lastTime += 1000L;
			this.frames = 0;
		}
	}
	
	@Unique
	private void updateFrameTimer() {
		long m = Util.getNanos();
		this.frameTimer.logFrameDuration(m - this.lastNanoTime);
		this.lastNanoTime = m;
	}
	
	@Unique
	private void setQuitScreen(Component reason, boolean isLocal) {
		TitleScreen ts = new TitleScreen();
		Screen s = isLocal ? ts : new JoinMultiplayerScreen(ts);
		
		this.setScreen(reason == null ? s : new DisconnectedScreen(s, new TranslatableComponent("disconnect.lost"), reason));
	}
	
	@Override
	public void wolftail_loadContext(ImplUPT type, UUID id, String name, Connection connect) throws InterruptedException, ExecutionException {
		(this.loadContextTask = new FutureTask<Void>(() -> {
			ImplPCC context = playContext = new ImplPCC(type, id, name, connect);
			SectionHandler.on_client_playing_change();
			
			logger.info("The universal player type in use is {}", type.registeringId());
			
			if(!type.isPlayerType()) {
				Screen s = screen;
				
				if(s != null) {
					s.removed();
					
					screen = null;
				}
				
				connect.setListener(new NptClientPacketListener(connect));
				type.callClientEnter(context);
			}
		}, null)).get();
	}
	
	@Unique
	private void unloadContext() {
		SectionHandler.on_client_playing_change();
		
		this.playContext = null;
	}
}
