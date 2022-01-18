package net.wolftail.impl.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.WorldServer;
import net.wolftail.impl.ExtensionsWorldServer;
import net.wolftail.impl.ImplWS;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer implements ExtensionsWorldServer {
	
	@Unique
	private ImplWS world_subscriber;
	
	@Inject(method = "<init>", at = @At("RETURN"))
	private void onConstruct(CallbackInfo info) {
		this.world_subscriber = new ImplWS();
	}
	
	@Override
	public ImplWS wolftail_getWorldSubscriber() {
		return this.world_subscriber;
	}
}
