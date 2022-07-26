package net.wolftail.impl.core.mixin.server;

import java.io.IOException;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.wolftail.impl.core.ImplMPCR;
import net.wolftail.impl.core.SectionHandler;

//SectionHandler.finish_loading; init root manager
@Mixin(DedicatedServer.class)
public abstract class MixinDedicatedServer extends net.wolftail.impl.core.mixin.MixinMinecraftServer {
	
	@Inject(method = "init", at = @At("RETURN"))
	private void onInit(CallbackInfoReturnable<Boolean> info) throws IOException {
		SectionHandler.finish_loading(true);
		
		this.root = new ImplMPCR((MinecraftServer) (Object) this);
	}
}
