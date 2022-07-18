package net.wolftail.impl.core.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.wolftail.api.lifecycle.PhysicalType;
import net.wolftail.impl.core.SectionHandler;

//SectionHandler.finish_preparing; SectionHandler.dedicatedServerRegularThread
@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {
	
	@Shadow
	private Thread serverThread;
	
	@Inject(method = "startServerThread", at = @At(value = "INVOKE", target = "start()V"))
	private void onStartServerThread(CallbackInfo info) {
		if(PhysicalType.DEDICATED_SERVER.is()) {
			//invoked by the 'zero' thread, transfer the host to the now unstarted serverThread
			
			SectionHandler.dedicatedServerRegularThread = this.serverThread;
		}
	}
	
	@Inject(method = "main", at = @At("HEAD"))
	private static void onMain(CallbackInfo info) {
		SectionHandler.finish_preparing();
	}
}
