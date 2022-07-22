package net.wolftail.impl.core.mixin.client;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.Connection;
import net.wolftail.impl.core.ExtCoreMinecraft;
import net.wolftail.impl.core.ImplPCC;
import net.wolftail.impl.core.ImplUPT;
import net.wolftail.impl.core.SectionHandler;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements ExtCoreMinecraft {
	
	@Shadow
	public Screen screen;
	
	@Unique
	private static final Logger logger = LogManager.getLogger("Wolftail/User");
	
	@Unique
	private FutureTask<Void> loadContextTask;
	
	@Unique
	private ImplPCC playContext;
	
	@Inject(method = "tick", at = @At(value = "INVOKE_STRING", target = "popPush(Ljava/lang/String;)V", args = { "ldc=textures" }))
	private void on_tick_invoke_popPush_0(CallbackInfo info) {
		ImplPCC context = this.playContext;
		
		if(context != null && !context.isConnected())
			this.unloadContext(); //steve quit game
	}
	
	@Inject(method = "runTick", at = @At("HEAD"), cancellable = true)
	private void on_runTick_head(CallbackInfo ci) {
		FutureTask<Void> task = this.loadContextTask;
		if(task != null) {
			task.run();
			
			this.loadContextTask = null;
		}
		
		ImplPCC context = this.playContext;
		ImplUPT type;
		
		if(context != null && !(type = context.playType()).isPlayerType()) {
			ci.cancel();
			
			try {
				this.doGameLoop(context, type);
			} catch(OutOfMemoryError e) {
				this.unloadContext();
				
				throw e;
			}
		}
	}
	
	@Unique
	private void doGameLoop(ImplPCC context, ImplUPT type) {
		
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
				
				connect.setListener(null);
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
