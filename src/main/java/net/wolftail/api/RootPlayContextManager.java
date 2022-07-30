package net.wolftail.api;

import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraft.server.MinecraftServer;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.Sealed;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.internal.core.ExtCoreMinecraftServer;

/**
 * Similar to {@link net.minecraft.server.players.PlayerList PlayerList}, except
 * player list is the manager of all {@link UniversalPlayerType#TYPE_PLAYER
 * steves}, but this is the manager of all uniplayer types.
 */
@Sealed
@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_SERVER)
public interface RootPlayContextManager {
	
	/**
	 * Every server has an unique root context manager.
	 * 
	 * @return the server the manager belongs to
	 */
	@Nonnull
	MinecraftServer server();
	
	/**
	 * Find a playing context by its id.
	 * 
	 * @return the playing context, null when non-exists
	 */
	PlayContext contextFor(UUID playId);
	
	/**
	 * How many uniplayers are playing in this moment.
	 * 
	 * @return the number of playing contexts
	 */
	int currentLoad();
	
	/**
	 * Identical to {@code server().getMaxPlayers()}, the number written in file
	 * 'server.properties'.
	 * 
	 * @return the 'max-players'
	 */
	int maxLoad();
	
	/**
	 * Get the sub manager governing the given type.
	 * 
	 * @return null only if {@code type} is null
	 */
	SubPlayContextManager subManager(UniversalPlayerType type);
	
	@Nonnull
	static RootPlayContextManager instanceFor(@Nonnull MinecraftServer server) {
		return ((ExtCoreMinecraftServer) server).wolftail_getRootManager();
	}
}
