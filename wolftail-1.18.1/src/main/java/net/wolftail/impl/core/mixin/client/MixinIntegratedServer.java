package net.wolftail.impl.core.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.server.IntegratedServer;

//prevent publishServer raise NPE
@Mixin(IntegratedServer.class)
public abstract class MixinIntegratedServer {
	
	@Redirect(method = "publishServer", at = @At(value = "INVOKE", target = "getGameProfile"))
	private GameProfile proxy_publishServer_getGameProfile(LocalPlayer p) {
		return Minecraft.getInstance().getUser().getGameProfile();
	}
	
	@Redirect(method = "publishServer", at = @At(value = "INVOKE", target = "setPermissionLevel"))
	private void proxy_publishServer_setPermissionLevel(LocalPlayer p, int j) {
		if(p != null) p.setPermissionLevel(j);
	}
}
