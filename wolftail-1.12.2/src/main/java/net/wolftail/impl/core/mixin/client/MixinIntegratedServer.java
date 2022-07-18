package net.wolftail.impl.core.mixin.client;

import java.io.IOException;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.wolftail.impl.core.ImplMPCR;
import net.wolftail.impl.core.mixin.MixinMinecraftServer;

//init root manager
@Mixin(IntegratedServer.class)
public abstract class MixinIntegratedServer extends MixinMinecraftServer {
	
	@Inject(method = "init", at = @At("RETURN"))
	private void onInit(CallbackInfoReturnable<Boolean> info) throws IOException {
		this.root = new ImplMPCR((MinecraftServer) (Object) this);
	}
}
