package net.wolftail.impl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.wolftail.api.lifecycle.PhysicalType;
import net.wolftail.impl.ExtensionsMinecraftServer;
import net.wolftail.impl.ImplMPCRoot;
import net.wolftail.impl.SharedImpls;

//rootPlayContextManager addition
@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer implements ExtensionsMinecraftServer {
	
	@Unique
	protected ImplMPCRoot root;
	
	@Inject(method = "<init>", at = @At("RETURN"))
	private void onConstruct(CallbackInfo info) {
		if(PhysicalType.INTEGRATED_CLIENT.is())
			this.root = new ImplMPCRoot(SharedImpls.as(this));
	}
	
	@Override
	public ImplMPCRoot wolftail_getRootManager() {
		return this.root;
	}
}
