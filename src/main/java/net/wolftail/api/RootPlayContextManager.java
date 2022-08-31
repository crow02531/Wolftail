package net.wolftail.api;

import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import com.google.common.base.Predicates;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.Sealed;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.internal.core.ExtCoreMinecraftServer;

/**
 * Similar to vanilla {@code PlayerList} except player list is the manager of
 * all {@link UniversalPlayerType#TYPE_PLAYER steves}, but this is the manager
 * of all uniplayers.
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
	 * Identical to {@code sendChat(type, text, null)}.
	 */
	default void sendChat(@Nonnull ChatType type, @Nonnull ITextComponent text) {
		this.sendChat(type, text, Predicates.alwaysTrue());
	}
	
	/**
	 * Send a chat to all play contexts matching {@code filter}.
	 * 
	 * @param type   the type of the chat
	 * @param text   the content of the chat
	 * @param filter the filter, null equals to {@code () -> true}
	 */
	void sendChat(@Nonnull ChatType type, @Nonnull ITextComponent text, Predicate<PlayContext> filter);
	
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
