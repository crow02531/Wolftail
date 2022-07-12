package net.wolftail.util.tracker;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;

/**
 * Represent a content in the server. For example, 'All blocks of the chunk
 * [chunkPos=(0, 0), dim=overworld]' is a content in server, and can be represented
 * as {@code ContentType.orderBlock(DimensionType.OVERWORLD, 0, 0)}.
 * 
 * <p>
 * Every content order has two parts. The first is its {@link #type()}, and the
 * second is its params.
 * </p>
 * 
 * @see ContentTracker
 * @see ContentType
 */
@Immutable
@SideWith(section = GameSection.GAME_PLAYING)
public abstract class ContentOrder {
	
	ContentOrder() {}
	
	@Nonnull
	public abstract ContentType type();
	
	@Override
	public abstract int hashCode();
	
	@Override
	public abstract boolean equals(Object o);
}
