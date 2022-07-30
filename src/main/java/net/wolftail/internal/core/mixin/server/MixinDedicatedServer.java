package net.wolftail.internal.core.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.dedicated.DedicatedServer;
import net.wolftail.internal.core.ImplMPCR;
import net.wolftail.internal.core.SectionHandler;

//SH: finish_loading, capture dedicatedServer
//root manager: init root manager
@Mixin(DedicatedServer.class)
public abstract class MixinDedicatedServer extends net.wolftail.internal.core.mixin.MixinMinecraftServer {
	
	@Inject(method = "init", at = @At(value = "INVOKE", target = "loadAllWorlds"))
	private void on_init_invoke_loadAllWorlds(CallbackInfoReturnable<Boolean> cir) {
		SectionHandler.finish_loading(true);
		this.root = new ImplMPCR(SectionHandler.dedicatedServer = (DedicatedServer) (Object) this);
	}
}
