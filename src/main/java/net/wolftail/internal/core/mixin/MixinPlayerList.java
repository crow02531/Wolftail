package net.wolftail.internal.core.mixin;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.stats.StatisticsManagerServer;
import net.wolftail.internal.core.ExtCoreMinecraftServer;
import net.wolftail.internal.core.ExtCorePlayerList;

//'The server is full!'
@Mixin(PlayerList.class)
public abstract class MixinPlayerList implements ExtCorePlayerList {
	
	@Final
	@Shadow
	public MinecraftServer mcServer;
	
	@Final
	@Shadow
	public Map<UUID, StatisticsManagerServer> playerStatFiles;
	
	@Final
	@Shadow
	public Map<UUID, PlayerAdvancements> advancements;
	
	@Redirect(method = "allowUserToConnect", at = @At(value = "INVOKE", target = "java.util.List.size()I", remap = false))
	private int proxy_allowUserToConnect_size(List<EntityPlayerMP> list) {
		return ((ExtCoreMinecraftServer) this.mcServer).wolftail_getRootManager().currentLoad();
	}
	
	@Override
	public void wolftail_rem_playerStatFiles(UUID id) {
		this.playerStatFiles.remove(id);
	}
	
	@Override
	public void wolftail_rem_advancements(UUID id) {
		this.advancements.remove(id);
	}
}
