package net.wolftail.impl.mixin.server;

import java.io.IOException;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.dedicated.DedicatedServer;
import net.wolftail.impl.ImplMPCRoot;
import net.wolftail.impl.SharedImpls;

@Mixin(DedicatedServer.class)
public abstract class MixinDedicatedServer extends net.wolftail.impl.mixin.MixinMinecraftServer {
	
	@Inject(method = "init", at = @At("RETURN"))
	private void onInit(CallbackInfoReturnable<Boolean> info) throws IOException {
		SharedImpls.H1.finish_loading(true);
		
		this.root = new ImplMPCRoot(SharedImpls.as(this));
	}
}
