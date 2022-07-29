package net.wolftail.impl.core.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.dedicated.DedicatedServer;
import net.wolftail.impl.core.SectionHandler;

//SH: finish_loading, capture dedicatedServer
@Mixin(DedicatedServer.class)
public abstract class MixinDedicatedServer {
	
	@Inject(method = "init", at = @At("RETURN"))
	private void on_init_return(CallbackInfoReturnable<Boolean> cir) {
		SectionHandler.dedicatedServer = (DedicatedServer) (Object) this;
		SectionHandler.finish_loading(true);
	}
}
