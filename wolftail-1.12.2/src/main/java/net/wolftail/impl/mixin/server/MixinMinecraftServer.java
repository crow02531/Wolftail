package net.wolftail.impl.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.wolftail.api.lifecycle.PhysicalType;
import net.wolftail.impl.SharedImpls;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {
	
	@Shadow
	private Thread serverThread;
	
	@Inject(method = "startServerThread", at = @At(value = "INVOKE", target = "start()V"))
	private void onStartServerThread(CallbackInfo info) {
		if(PhysicalType.DEDICATED_SERVER.is()) {
			//invoked by the 'zero' thread, transfer the host to the now unstarted serverThread
			
			SharedImpls.H1.regular_dedicated_server_host = this.serverThread;
		}
	}
	
	@Inject(method = "main", at = @At("HEAD"))
	private static void onMain(CallbackInfo info) {
		SharedImpls.H1.finish_preparing();
	}
}
