package net.wolftail.internal.core.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.server.integrated.IntegratedServer;
import net.wolftail.internal.core.ImplMPCR;
import net.wolftail.internal.core.mixin.MixinMinecraftServer;

//prevent shareToLAN raises NPE
//root manager: init root manager
@Mixin(IntegratedServer.class)
public abstract class MixinIntegratedServer extends MixinMinecraftServer {
	
	@Inject(method = "<init>*", at = @At("RETURN"))
	private void on_$ctor_return(CallbackInfo ci) {
		this.root = new ImplMPCR((IntegratedServer) (Object) this);
	}
	
	@Redirect(method = "shareToLAN", at = @At(value = "INVOKE", target = "net.minecraft.client.entity.EntityPlayerSP.setPermissionLevel(I)V"))
	private void proxy_shareToLAN_setPermissionLevel(EntityPlayerSP sp, int i) {
		if (sp != null)
			sp.setPermissionLevel(i);
	}
}
