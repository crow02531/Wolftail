package net.wolftail.util.tracker;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import net.minecraft.server.MinecraftServer;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.internal.tracker.Mechanisms;

/**
 * An order asking what you want to subscribe in the server.
 * 
 * @see ContentTracker
 */
@Immutable
public abstract class ContentOrder {
	
	/**
	 * Every tick when {@code timing} is matched, the regarding changes in
	 * {@code server} will be sent to {@code acceptor} if necessary. You shouldn't
	 * call this method during assemble stage.
	 * 
	 * <p>
	 * The changes will be sent by logic server during the assemble stage of the
	 * tick. The first data {@code acceptor} received is the content itself.
	 * </p>
	 * 
	 * @param server   the server
	 * @param acceptor the change acceptor
	 * @param timing   the timing
	 * 
	 * @return true if {@code acceptor} hasn't tracked {@code this} before , two
	 *         acceptors {@code a} and {@code b} are regarded the same if and only
	 *         if {@code a == b}
	 */
	@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_SERVER)
	public abstract boolean track(@Nonnull MinecraftServer server, @Nonnull DiffVisitor acceptor,
			@Nonnull Timing timing);
	
	/**
	 * Cancel the track request. You shouldn't call this method during assemble
	 * stage.
	 * 
	 * @param server   the server
	 * @param acceptor the acceptor
	 * 
	 * @return true if {@code acceptor} has tracked {@code this} before , two
	 *         acceptors {@code a} and {@code b} are regarded the same if and only
	 *         if {@code a == b}
	 */
	@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_SERVER)
	public abstract boolean untrack(@Nonnull MinecraftServer server, @Nonnull DiffVisitor acceptor);
	
	@Override
	public abstract int hashCode();
	
	@Override
	public abstract boolean equals(Object o);
	
	/**
	 * Add a assemble mechanism that will be run by logic server during server
	 * assembling. Duplicate adding will be ignored.
	 * 
	 * @param r the mechanism, two mechanisms {@code a} and {@code b} are regarded
	 *          the same if and only if {@code a == b}
	 */
	public static void addMechanism(@Nonnull Runnable r) {
		Mechanisms.add(r);
	}
}
