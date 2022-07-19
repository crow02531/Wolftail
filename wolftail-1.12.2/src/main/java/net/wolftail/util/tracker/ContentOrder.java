package net.wolftail.util.tracker;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.apache.http.entity.ContentType;

import net.minecraft.server.MinecraftServer;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;

/**
 * An order asking what you want to subscribe in the server.
 * 
 * @see ContentTracker
 * @see ContentType
 */
@Immutable
@SideWith(section = GameSection.GAME_PLAYING)
public abstract class ContentOrder {
	
	/**
	 * Every tick when {@code timing} is matched, the regarding changes in
	 * {@code server} will be sent to {@code acceptor} if necessary.
	 * 
	 * <p>
	 * The changes MUST be sent by logic server during the assemble stage
	 * of the tick.
	 * </p>
	 * 
	 * @param server	the server
	 * @param acceptor	the change acceptor
	 * @param timing	the timing
	 * 
	 * @return true if {@code acceptor} hasn't tracked {@code this} before
	 * 		, two acceptors {@code a} and {@code b} are regarded the same if
	 * 		and only if {@code a == b}
	 * 
	 * @throws IllegalStateException	when called during assemble stage
	 */
	@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_SERVER)
	public abstract boolean track(@Nonnull MinecraftServer server, @Nonnull DiffVisitor acceptor, @Nonnull Timing timing);
	
	/**
	 * Cancel the track request.
	 * 
	 * @param server	the server
	 * @param acceptor	the acceptor
	 * 
	 * @return true if {@code acceptor} has tracked {@code this} before
	 * 		, two acceptors {@code a} and {@code b} are regarded the same
	 * 		if and only if {@code a == b}
	 * 
	 * @throws IllegalStateException	when called during assemble stage
	 */
	@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_SERVER)
	public abstract boolean untrack(@Nonnull MinecraftServer server, @Nonnull DiffVisitor acceptor);
	
	@Override
	public abstract int hashCode();
	
	@Override
	public abstract boolean equals(Object o);
}
