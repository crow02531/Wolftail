package net.wolftail.impl.core.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.server.integrated.IntegratedServer;

//prevent shareToLAN raises NPE
@Mixin(IntegratedServer.class)
public abstract class MixinIntegratedServer {
	
	@Redirect(method = "shareToLAN", at = @At(value = "INVOKE", target = "setPermissionLevel"))
	private void proxy_shareToLAN_setPermissionLevel(EntityPlayerSP sp, int i) {
		if (sp != null)
			sp.setPermissionLevel(i);
	}
}
