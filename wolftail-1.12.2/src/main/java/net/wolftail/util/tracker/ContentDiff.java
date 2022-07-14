package net.wolftail.util.tracker;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableSet;

import io.netty.buffer.ByteBuf;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.impl.ImplCD;
import net.wolftail.impl.SharedImpls.H4;

@Immutable
@SideWith(section = GameSection.GAME_PLAYING)
public interface ContentDiff {
	
	/**
	 * The orders regarding the changes.
	 * 
	 * @return a non-empty immutable set
	 */
	@Nonnull ImmutableSet<ContentOrder> orders();
	
	/**
	 * Transfer the whole content diff into a newly created
	 * read-only byte buf. Don't worry about its performance.
	 * 
	 * @return a newly created read-only buf
	 */
	@Nonnull ByteBuf toByteBuf();
	
	/**
	 * Apply the content diff to the given partial universe.
	 * 
	 * @see #apply(ByteBuf, SlaveUniverse)
	 */
	void apply(@Nonnull SlaveUniverse dst);
	
	int hashCode();
	
	boolean equals(Object o);
	
	/**
	 * Create a new content diff from the buf's all readable bytes.
	 * 
	 * @return the content diff
	 */
	@Nonnull
	public static ContentDiff from(@Nonnull ByteBuf buf) {
		buf = buf.copy().asReadOnly();
		
		ContentType types[] = ContentType.values();
		ImmutableSet.Builder<ContentOrder> orders = ImmutableSet.builder();
		
		while(buf.isReadable()) orders.add(types[H4.readVarInt(buf)].check(buf));
		
		return new ImplCD(orders.build(), buf.readerIndex(0));
	}
	
	/**
	 * Similar to {@code from(buf).apply(dst)}, except this method analyzes the
	 * {@code buf} directly without any copy and extra operation. Thus it has
	 * higher performance.
	 */
	public static void apply(@Nonnull ByteBuf buf, @Nonnull SlaveUniverse dst) {
		ContentType types[] = ContentType.values();
		
		while(buf.isReadable()) types[H4.readVarInt(buf)].apply(buf, dst);
	}
}
