package net.wolftail.impl.core.mixin.client;

import java.util.Locale;
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
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.Timer;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FrameTimer;
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
	
	@Final
	@Shadow
	public KeyboardHandler keyboardHandler;
	
	@Final
	@Shadow
	public MouseHandler mouseHandler;
	
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
	public abstract void stop();
	
	@Shadow
	public abstract void setScreen(Screen screen);
	
	@Shadow
	public abstract float getDeltaFrameTime();
	
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
	
	@Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
	private void on_setScreen_head(Screen s, CallbackInfo ci) {
		if(ExtCoreMinecraft.isNptPlaying()) {
			ci.cancel();
			
			if(this.screen != null)
				this.screen.removed();
			
			this.screen = s;
			BufferUploader.reset();
			
			if(s != null) {
				this.mouseHandler.releaseMouse();
				KeyMapping.releaseAll();
				s.init(Minecraft.getInstance(), this.window.getGuiScaledWidth(), this.window.getGuiScaledHeight());
			}
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
		this.runAllTasks(); //process mouse&keyboard events, network packets .etc.
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
				
				if(this.screen != null)
					Screen.wrapScreenError(() -> this.screen.tick(), "Ticking screen", this.screen.getClass().getCanonicalName());
				
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
			
			this.keyboardHandler.tick(); //trigger manual crash
		}
		
		this.profiler.pop();
		//----------------------------------Tick End----------------------------------
		
		this.window.setErrorSection("Render");
		
		//--------------------------------Render Start--------------------------------
		this.profiler.push("render");
		
		PoseStack poseStack = RenderSystem.getModelViewStack();
		poseStack.pushPose();
		RenderSystem.applyModelViewMatrix();
		
		this.mainRenderTarget.bindWrite(true); //bind framebuffer & set viewport
		
		//do frame
		if(!lostContext) {
			context.playType().callClientFrame();
			
			if(this.screen != null) {
				RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
				RenderSystem.setProjectionMatrix(Matrix4f.orthographic(0, (float) ((double) this.window.getWidth() / this.window.getGuiScale()), 0, (float) ((double) this.window.getHeight() / this.window.getGuiScale()), 1000, 3000));
				poseStack.setIdentity();
				poseStack.translate(0.0, 0.0, -2000.0);
				RenderSystem.applyModelViewMatrix();
				Lighting.setupFor3DItems();
				
				callScreen(this.screen, this.mouseHandler.xpos(), this.mouseHandler.ypos(), this.getDeltaFrameTime(), this.window);
			}
		}
		
		this.mainRenderTarget.unbindWrite(); //unbind framebuffer
		
		poseStack.popPose();
		
		poseStack.pushPose();
		RenderSystem.applyModelViewMatrix();
		this.mainRenderTarget.blitToScreen(this.window.getWidth(), this.window.getHeight());
		poseStack.popPose();
		
		this.profiler.pop();
		//---------------------------------Render End---------------------------------
		
		this.window.updateDisplay();
		if((i = this.window.getFramerateLimit()) < Option.FRAMERATE_LIMIT.getMaxValue())
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
	private static void callScreen(Screen s, double xpos, double ypos, float f, Window w) {
		int i = (int) (xpos * (double) w.getGuiScaledWidth() / (double) w.getScreenWidth());
		int j = (int) (ypos * (double) w.getGuiScaledHeight() / (double) w.getScreenHeight());
		
		try {
			s.render(new PoseStack(), i, j, f);
		} catch(Throwable e) {
			CrashReport crashReport = CrashReport.forThrowable(e, "Rendering screen");
			CrashReportCategory crashReportCategory = crashReport.addCategory("Screen render details");
			crashReportCategory.setDetail("Screen name", () -> s.getClass().getCanonicalName());
			crashReportCategory.setDetail("Mouse location", () -> String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", i, j, xpos, ypos));
			crashReportCategory.setDetail("Screen size", () -> String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f", w.getGuiScaledWidth(), w.getGuiScaledHeight(), w.getWidth(), w.getHeight(), w.getGuiScale()));
			
			throw new ReportedException(crashReport);
        }
		
		try {
			s.handleDelayedNarration();
        } catch(Throwable e) {
        	CrashReport crashReport = CrashReport.forThrowable(e, "Narrating screen");
        	crashReport.addCategory("Screen details").setDetail("Screen name", () -> s.getClass().getCanonicalName());
        	
        	throw new ReportedException(crashReport);
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
	
	@Unique
	private void unloadContext() {
		SectionHandler.on_client_playing_change();
		
		this.playContext = null;
	}
	
	@Override
	public void wolftail_loadContext(ImplUPT type, UUID id, String name, Connection connect) throws InterruptedException, ExecutionException {
		(this.loadContextTask = new FutureTask<Void>(() -> {
			ImplPCC context = playContext = new ImplPCC(type, id, name, connect);
			SectionHandler.on_client_playing_change();
			
			logger.info("The universal player type in use is {}", type.registeringId());
			
			if(!type.isPlayerType()) {
				screen.removed();
				screen = null;
				BufferUploader.reset();
				
				connect.setListener(new NptClientPacketListener(connect));
				type.callClientEnter(context);
			}
		}, null)).get();
	}
	
	@Override
	public ImplPCC wolftail_getContext() {
		return this.playContext;
	}
}
