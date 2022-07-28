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
public interface ContentTracker {
	
	/**
	 * Subscribe a specific content(represented as {@code order}) in server. The
	 * change will be sent, if necessary, to {@code subscriber} when {@code timing}
	 * is met. Notice that it will be sent only by logic server during the assemble
	 * stage of the tick. If one subscriber subscribes more than one content, it
	 * will still get at most one content diff when {@code timing} is met.
	 * 
	 * <p>
	 * The first content diff {@code subscriber} received is the content itself.
	 * </p>
	 * 
	 * @param order			the request
	 * @param subscriber	the subscriber
	 * @param timing		the timing
	 * 
	 * @return true if {@code subscriber} hasn't subscribe {@code order} before,
	 * 		two subscribers {@code a} and {@code b} are regarded the same if and
	 * 		only if {@code a == b}
	 * 
	 * @throws IllegalStateException	when called during assemble stage
	 */
	boolean subscribe(@Nonnull ContentOrder order, @Nonnull Consumer<ContentDiff> subscriber, @Nonnull Timing timing);
	
	/**
	 * Cancel the subscribe.
	 * 
	 * @param order			the request
	 * @param subscriber	the subscriber
	 * 
	 * @return true if {@code subscriber} has subscribed {@code order} before,
	 * 		two subscribers {@code a} and {@code b} are regarded the same if and
	 * 		only if {@code a == b}
	 * 
	 * @throws IllegalStateException	when called during assemble stage
	 */
	boolean unsubscribe(@Nonnull ContentOrder order, @Nonnull Consumer<ContentDiff> subscriber);
	
	/**
	 * Check if it is in assemble stage. Assemble stage is the very end
	 * of a tick.
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
}
