package net.wolftail.impl.mixin;

import java.net.SocketAddress;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.wolftail.impl.ImplMPCRoot;
import net.wolftail.impl.SharedImpls;

//'The server is full!'
@Mixin(PlayerList.class)
public abstract class MixinPlayerList {
	
	@Final
	@Shadow
	private MinecraftServer mcServer;
	
	@Shadow
	public abstract boolean bypassesPlayerLimit(GameProfile profile);
	
	@Inject(method = "allowUserToConnect", at = @At("RETURN"))
	private void onAllowUserToConnect(SocketAddress address, GameProfile profile, CallbackInfoReturnable<String> info) {
		if(info.getReturnValue() == null) {
			ImplMPCRoot rm = SharedImpls.as(this.mcServer).wolftail_getRootManager();
			
			if(rm.currentLoad() >= rm.maxLoad() && !this.bypassesPlayerLimit(profile))
				info.setReturnValue("The server is full!");
		}
	}
}
