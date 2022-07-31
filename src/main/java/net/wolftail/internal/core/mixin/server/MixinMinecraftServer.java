package net.wolftail.internal.core.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.wolftail.api.lifecycle.PhysicalType;
import net.wolftail.internal.core.SectionHandler;

//SH: finish_preparing, capture dedicatedServerRegularThread
@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {
	
	@Shadow
	public Thread serverThread;
	
	@Inject(method = "startServerThread", at = @At(value = "INVOKE", target = "java.lang.Thread.start()V", remap = false))
	private void on_startServerThread_invoke_start(CallbackInfo ci) {
		if (PhysicalType.DEDICATED_SERVER.is()) {
			// invoked by the 'zero' thread, transfer the host to the now unstarted
			// serverThread
			
			SectionHandler.dedicatedServerRegularThread = this.serverThread;
		}
	}
	
	@Inject(method = "main", at = @At("HEAD"), remap = false)
	private static void on_main_head(CallbackInfo ci) {
		SectionHandler.finish_preparing();
	}
}
