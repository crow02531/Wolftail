package net.wolftail.util.tracker;

import java.util.function.Consumer;

import javax.annotation.Nonnull;

import net.minecraft.server.MinecraftServer;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.impl.ExtensionsMinecraftServer;
import net.wolftail.impl.SharedImpls;
import net.wolftail.impl.SharedImpls.H3;

@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_SERVER)
public final class ContentTracker {
	
	private final MinecraftServer server;
	
	private ContentTracker(MinecraftServer arg0) {
		this.server = arg0;
	}
	
	/**
	 * It is identical to {@code subscribe(order, subscriber, 1)}. The change will be
	 * sent to you every tick if necessary.
	 * 
	 * @param order			the content in interest
	 * @param subscriber	the subscriber
	 * 
	 * @throws IllegalArgumentException	thrown when the {@code subscriber} has
	 * 		subscribed the {@code order}
	 * 
	 * @see #subscribe(ContentOrder, Consumer, int)
	 */
	public void subscribe(@Nonnull ContentOrder order, @Nonnull Consumer<ContentDiff> subscriber) {
		this.subscribe(order, subscriber, 1);
	}
	
	/**
	 * Subscribe a specific content in the server. The change will be sent to you
	 * every some ticks(at a specific frequency) if necessary. Notice that the change
	 * will be sent only by logic server during the post tick.
	 * 
	 * <p>
	 * For example, {@code subscribe(orderBlock(OVERWORLD, 0, 0), mySubs, 2)}
	 * has the following effect.
	 * </p>
	 * 
	 * <pre>
	 *   +-------------------+-------------------+-------------------+
	 *   |     tick 123      |     tick 124      |     tick 125      |
	 *   |              |POST|              |POST|              |POST|
	 *   +-------------------+-------------------+-------------------+
	 *          ↑          ↑                                      ↑
	 *  subscribe called   |                                      |
	 *                     |                                      |
	 *            mySubs received data                   mySubs received data
	 * </pre>
	 * 
	 * @param order			the content in interest
	 * @param subscriber	the subscriber
	 * @param frequency		in the unit of tick, must larger than 0
	 * 
	 * @throws IllegalArgumentException	thrown when the {@code subscriber} has
	 * 		subscribed the {@code order}
	 */
	public void subscribe(@Nonnull ContentOrder order, @Nonnull Consumer<ContentDiff> subscriber, int frequency) {
		order.type().subscribe(this.server, order, new H3(subscriber, this.server.getTickCounter(), frequency));
	}
	
	/**
	 * Cancel the subscribe.
	 * 
	 * @param order			the content subscribed
	 * @param subscriber	the subscriber
	 * 
	 * @see #subscribe(ContentOrder, Consumer, int)
	 */
	public void unsubscribe(@Nonnull ContentOrder order, @Nonnull Consumer<ContentDiff> subscriber) {
		order.type().unsubscribe(this.server, order, subscriber);
	}
	
	@Nonnull
	public static ContentTracker instanceFor(@Nonnull MinecraftServer server) {
		ExtensionsMinecraftServer ext = SharedImpls.as(server);
		ContentTracker r = ext.wolftail_getContentTracker();
		
		if(r == null) {
			synchronized(LOCK) {
				r = ext.wolftail_getContentTracker();
				
				if(r == null)
					ext.wolftail_setContentTracker(r = new ContentTracker(server));
			}
		}
		
		return r;
	}
	
	private static final Object LOCK = new Object();
}
