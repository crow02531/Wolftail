package net.wolftail.util.tracker;

import java.util.IdentityHashMap;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import net.minecraft.server.MinecraftServer;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.impl.ExtensionsMinecraftServer;
import net.wolftail.impl.SharedImpls;
import net.wolftail.impl.SharedImpls.H3;
import net.wolftail.impl.SharedImpls.H6;

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
	 * Shortcut for {@code subscribe(order, subscriber, 1)}. The change will be
	 * sent to you every tick if necessary.
	 * 
	 * @param order			the content in interest
	 * @param subscriber	the subscriber
	 * 
	 * @throws IllegalArgumentException	thrown when the {@code subscriber} has
	 * 		subscribed the {@code order}
	 * @throws IllegalStateException	thrown if called by {@code subscriber::accept}
	 * 
	 * @see #subscribe(ContentOrder, Consumer, int)
	 */
	public void subscribe(@Nonnull ContentOrder order, @Nonnull Consumer<ContentDiff> subscriber) {
		this.subscribe(order, subscriber, 1);
	}
	
	/**
	 * Subscribe a specific content in the server. The change will be sent to
	 * {@code subscriber} every {@code interval} ticks if necessary. Notice that
	 * the change will be sent only by logic server during the post tick, and
	 * if your subscriber subscribes more than one content, it will still get at
	 * most one content diff(a composite one) every tick.
	 * 
	 * <p>
	 * For example, codes
	 * 
	 * <pre>
	 *   o0 = orderBlock(OVERWORLD, 0, 0);
	 *   o1 = orderBlock(OVERWORLD, 0, 1);
	 *   
	 *   subscribe(o0, mySubs, 2);
	 *   subscribe(o1, mySubs, 2);
	 * </pre>
	 * 
	 * may has the following effect.
	 * 
	 * <pre>
	 *   +-------------------+-------------------+-------------------+
	 *   |     tick 123      |     tick 124      |     tick 125      |
	 *   |              |POST|              |POST|              |POST|
	 *   +-------------------+-------------------+-------------------+
	 *          ↑          ↑                                      ↑
	 *    method called    |                                      |
	 *                     |                                      |
	 *      mySubs received ContentDiff(orders = o0, o1)          |
	 *                                                            |
	 *                                        mySubs received ContentDiff(orders = o0)
	 * </pre>
	 * 
	 * <p>
	 * Note that it dosen't mean the bigger interval the better performance.
	 * Generally a fast-changing content(like {@link ContentType#WORLD_DAYTIME
	 * WORLD_DAYTIME}) would perform better with big interval, while the
	 * slow-changing content(like {@link ContentType#CHUNK_BLOCK CHUNK_BLOCK})
	 * prefers small interval.
	 * 
	 * @param order			the content in interest
	 * @param subscriber	the subscriber
	 * @param interval		in the unit of tick, must larger than 0
	 * 
	 * @throws IllegalArgumentException	thrown when the {@code subscriber} has
	 * 		subscribed the {@code order}
	 * @throws IllegalStateException	thrown if called by {@code subscriber::accept}
	 */
	public void subscribe(@Nonnull ContentOrder order, @Nonnull Consumer<ContentDiff> subscriber, int interval) {
		IdentityHashMap<Consumer<ContentDiff>, H6> wrappers = SharedImpls.as(this.check()).wolftail_wrappers();
		
		H6 wrapper = wrappers.get(subscriber);
		if(wrapper == null) wrapper = new H6(subscriber);
		
		order.type().subscribe(this.server, order, new H3(wrapper, this.server.getTickCounter(), interval));
		
		if(wrapper.num++ == 0) wrappers.put(subscriber, wrapper);
	}
	
	/**
	 * Cancel the subscribe. Have no effect if the subscribe hasn't been made.
	 * 
	 * @param order			the content subscribed
	 * @param subscriber	the subscriber
	 * 
	 * @return true if the subscribe exists
	 * 
	 * @throws IllegalStateException	thrown if called by {@code subscriber::accept}
	 * 
	 * @see #subscribe(ContentOrder, Consumer, int)
	 */
	public boolean unsubscribe(@Nonnull ContentOrder order, @Nonnull Consumer<ContentDiff> subscriber) {
		H6 wrapper = SharedImpls.as(this.check()).wolftail_wrappers().get(subscriber);
		
		if(wrapper != null) {
			if(order.type().unsubscribe(this.server, order, wrapper)) {
				if(--wrapper.num == 0)
					SharedImpls.as(this.server).wolftail_wrappers().remove(subscriber);
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Get the unique content tracker of the given server.
	 * 
	 * @return the content tracker
	 */
	@Nonnull
	public static ContentTracker instanceFor(@Nonnull MinecraftServer server) {
		ExtensionsMinecraftServer ext = SharedImpls.as(server);
		ContentTracker r = ext.wolftail_getContentTracker();
		
		if(r == null) ext.wolftail_setContentTracker(r = new ContentTracker(server));
		
		return r;
	}
	
	private MinecraftServer check() {
		if(SharedImpls.as(this.server).wolftail_duringSending())
			throw new IllegalStateException();
		
		return this.server;
	}
}
