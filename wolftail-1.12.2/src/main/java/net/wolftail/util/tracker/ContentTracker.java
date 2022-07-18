package net.wolftail.util.tracker;

import java.util.function.Consumer;

import javax.annotation.Nonnull;

import net.minecraft.server.MinecraftServer;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;

/**
 * Every server, no matter it is integrated or dedicated, has an unique
 * content tracker. The content tracker was used for tracking content
 * in server.
 */
@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_SERVER)
public final class ContentTracker {
	
	private final MinecraftServer server;
	
	private ContentTracker(MinecraftServer arg0) {
		this.server = arg0;
	}
	
	public void subscribe(@Nonnull ContentOrder order, @Nonnull Consumer<ContentDiff> subscriber) {
		this.subscribe(order, subscriber, Timing.EVERY_TICK);
	}
	
	public boolean subscribe(@Nonnull ContentOrder order, @Nonnull Consumer<ContentDiff> subscriber, @Nonnull Timing timing) {
		return false;
	}
	
	public boolean unsubscribe(@Nonnull ContentOrder order, @Nonnull Consumer<ContentDiff> subscriber) {
		return false;
	}
	
	/**
	 * Check if it is in assemble stage.
	 * 
	 * @return true if the server was in assemble stage
	 */
	public boolean duringAssemble() {
		return false;
	}
	
	/**
	 * Get the unique content tracker of the given server.
	 * 
	 * @return the content tracker
	 */
	@Nonnull
	public static ContentTracker instanceFor(@Nonnull MinecraftServer server) {
		return null;
	}
}
