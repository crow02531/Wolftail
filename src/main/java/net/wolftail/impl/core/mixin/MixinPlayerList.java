package net.wolftail.impl.core.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.wolftail.impl.core.ExtCoreMinecraftServer;

//'The server is full!'
@Mixin(PlayerList.class)
public abstract class MixinPlayerList {
	
	@Final
	@Shadow
	public MinecraftServer mcServer;
	
	@Redirect(method = "allowUserToConnect", at = @At(value = "INVOKE", target = "size()I"))
	private int proxy_allowUserToConnect_size(List<EntityPlayerMP> list) {
		return ((ExtCoreMinecraftServer) this.mcServer).wolftail_getRootManager().currentLoad();
	}
}
