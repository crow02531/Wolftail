package net.wolftail.impl.core.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.wolftail.impl.core.ExtCoreMinecraftServer;

//'server is full'
@Mixin(PlayerList.class)
public abstract class MixinPlayerList {
	
	@Final
	@Shadow
	public MinecraftServer server;
	
	@Redirect(method = "canPlayerLogin", at = @At(value = "INVOKE", target = "size()I"))
	private int proxy_canPlayerLogin_size(List<ServerPlayer> list) {
		return ((ExtCoreMinecraftServer) this.server).wolftail_getRootManager().currentLoad();
	}
}
