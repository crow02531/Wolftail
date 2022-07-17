package net.wolftail.impl.mixin.client;

import java.io.IOException;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.integrated.IntegratedServer;
import net.wolftail.impl.ImplMPCR;
import net.wolftail.impl.SharedImpls;
import net.wolftail.impl.mixin.MixinMinecraftServer;

@Mixin(IntegratedServer.class)
public abstract class MixinIntegratedServer extends MixinMinecraftServer {
	
	@Inject(method = "init", at = @At("RETURN"))
	private void onInit(CallbackInfoReturnable<Boolean> info) throws IOException {
		this.root = new ImplMPCR(SharedImpls.as(this));
	}
}
