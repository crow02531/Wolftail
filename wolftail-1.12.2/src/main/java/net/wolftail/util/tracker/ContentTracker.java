package net.wolftail.util.tracker;

import java.util.IdentityHashMap;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import net.minecraft.server.MinecraftServer;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.impl.tracker.ExtTrackerMinecraftServer;
import net.wolftail.impl.tracker.SubscriberWrapper;

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
	
	/**
	 * Shortcut for {@code subscribe(order, subscriber, EVERY_TICK)}.
	 * 
	 * @param order			the request
	 * @param subscriber	the subscriber
	 * 
	 * @return true if {@code subscriber} hasn't subscribe {@code order} before
	 * 
	 * @throws IllegalStateException	when called during assemble stage
	 * 
	 * @see #subscribe(ContentOrder, Consumer, Timing)
	 */
	public void subscribe(@Nonnull ContentOrder order, @Nonnull Consumer<ContentDiff> subscriber) {
		this.subscribe(order, subscriber, Timing.EVERY_TICK);
	}
	
	/**
	 * Subscribe a specific content(represented as {@code order} in server. The
	 * change will be sent, if necessary, to {@code subscriber} when {@code timing}
	 * is met. The change will be sent only by logic server during the assemble
	 * stage of the tick. If one subscriber subscribes more than one content, it
	 * will still get at most one content diff when {@code timing} is met.
	 * 
	 * @param order			the request
	 * @param subscriber	the subscriber
	 * @param timing		the timing
	 * 
	 * @return true if {@code subscriber} hasn't subscribe {@code order} before
	 * 
	 * @throws IllegalStateException	when called during assemble stage
	 */
	public boolean subscribe(@Nonnull ContentOrder order, @Nonnull Consumer<ContentDiff> subscriber, @Nonnull Timing timing) {
		IdentityHashMap<Consumer<ContentDiff>, SubscriberWrapper> wrappers = ((ExtTrackerMinecraftServer) this.server).wolftail_wrappers();
		
		SubscriberWrapper w = wrappers.get(subscriber);
		if(w == null) w = new SubscriberWrapper(subscriber);
		
		if(order.track(this.server, w.getWriter(), timing)) {
			wrappers.put(subscriber, w);
			w.onSubscribe();
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Cancel the subscribe.
	 * 
	 * @param order			the request
	 * @param subscriber	the subscriber
	 * 
	 * @return true if {@code subscriber} has subscribed {@code order} before
	 * 
	 * @throws IllegalStateException	when called during assemble stage
	 */
	public boolean unsubscribe(@Nonnull ContentOrder order, @Nonnull Consumer<ContentDiff> subscriber) {
		SubscriberWrapper w = ((ExtTrackerMinecraftServer) this.server).wolftail_wrappers().get(subscriber);
		
		if(w != null && order.untrack(this.server, w.getWriter())) {
			if(w.onUnsubscribe())
				((ExtTrackerMinecraftServer) this.server).wolftail_wrappers().remove(subscriber);
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Check if it is in assemble stage. Assemble stage is the very end
	 * of a tick.
	 * 
	 * @return true if the server was in assemble stage
	 */
	public boolean duringAssemble() {
		return ((ExtTrackerMinecraftServer) this.server).wolftail_duringAssemble();
	}
	
	/**
	 * Get the unique content tracker of the given server.
	 * 
	 * @return the content tracker
	 */
	@Nonnull
	public static ContentTracker instanceFor(@Nonnull MinecraftServer server) {
		ContentTracker ct = ((ExtTrackerMinecraftServer) server).wolftail_getTracker();
		
		if(ct == null)
			((ExtTrackerMinecraftServer) server).wolftail_setTracker(ct = new ContentTracker(server));
		
		return ct;
	}
}
