package net.wolftail.util.tracker;

import javax.annotation.Nonnull;

import net.minecraft.server.MinecraftServer;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.Sealed;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.internal.tracker.Mechanisms;

/**
 * Every server, no matter it is integrated or dedicated, has an unique content
 * tracker.
 */
@Sealed
@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_SERVER)
public interface ContentTracker {
	
	/**
	 * Check if it is in assemble stage. Assemble stage is the very end of a tick.
	 * 
	 * @return true if the server was in assemble stage
	 */
	boolean inAssemble();
	
	/**
	 * Get the unique content tracker of the given server.
	 * 
	 * @return the content tracker
	 */
	@Nonnull
	static ContentTracker instanceFor(@Nonnull MinecraftServer server) {
		return (ContentTracker) server;
	}
	
	/**
	 * Add a assemble mechanism that will be run by logic server during server
	 * assembling. Duplicate adding will be ignored.
	 * 
	 * @param r the mechanism, two mechanisms {@code a} and {@code b} are regarded
	 *          the same if and only if {@code a == b}
	 */
	@SideWith
	static void addMechanism(@Nonnull Runnable r) {
		Mechanisms.add(r);
	}
}
