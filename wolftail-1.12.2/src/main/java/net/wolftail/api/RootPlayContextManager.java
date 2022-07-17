package net.wolftail.api;

import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraft.server.MinecraftServer;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.impl.SharedImpls;

/**
 * Similar to {@link net.minecraft.server.management.PlayerList PlayerList}, except
 * player list is the manager of all {@link UniversalPlayerType#TYPE_PLAYER steves},
 * but this is the manager of all uniplayer types.
 */
@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_SERVER)
public interface RootPlayContextManager {
	
	/**
	 * Every server has an unique root context manager.
	 * 
	 * @return the server the manager belongs to
	 */
	@Nonnull MinecraftServer server();
	
	/**
	 * Find a playing context by its id.
	 * 
	 * @return the playing context, null when non-exists
	 */
	ServerPlayContext contextFor(UUID playId);
	
	/**
	 * How many 'players' are playing in this moment.
	 * 
	 * @return the number of playing contexts
	 */
	int currentLoad();
	
	/**
	 * Identical to {@code server().getMaxPlayers()}, the number
	 * written in file 'server.properties'.
	 * 
	 * @return the 'max-players'
	 */
	int maxLoad();
	
	/**
	 * Get the sub manager governing the given type.
	 * 
	 * @return null only when {@code type} is null
	 */
	SubPlayContextManager subManager(UniversalPlayerType type);
	
	/**
	 * View the root manager as a set of sub managers.
	 * 
	 * @return an unmodifiable set, return the same one every call
	 */
	@Nonnull Set<SubPlayContextManager> asManagerSet();
	
	/**
	 * View the root manager as a set of playing contexts.
	 * 
	 * @return an unmodifiable view set, changes in the root manager
	 * 		are reflected
	 */
	@Nonnull Set<ServerPlayContext> asContextSet();
	
	@Nonnull
	static RootPlayContextManager instanceFor(@Nonnull MinecraftServer server) {
		return SharedImpls.as(server).wolftail_getRootManager();
	}
}
