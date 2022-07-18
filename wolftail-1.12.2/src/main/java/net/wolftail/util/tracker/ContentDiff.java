package net.wolftail.util.tracker;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import io.netty.buffer.ByteBuf;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;

@Immutable
@SideWith(section = GameSection.GAME_PLAYING)
public interface ContentDiff {
	
	/**
	 * Transfer the whole content diff into a newly created
	 * read-only byte buf. Don't worry about its performance.
	 * 
	 * @return a newly created read-only buf
	 */
	@Nonnull ByteBuf toByteBuf();
	
	/**
	 * Make the given visitor visit this content diff.
	 * 
	 * @see #apply(ByteBuf, DiffVisitor)
	 */
	void apply(@Nonnull DiffVisitor visitor);
	
	int hashCode();
	
	boolean equals(Object o);
	
	/**
	 * Create a new content diff from the buf's all readable bytes.
	 * 
	 * @return the new content diff
	 */
	@Nonnull
	public static ContentDiff from(@Nonnull ByteBuf buf) {
		buf = buf.copy().asReadOnly();
		
		return null;
	}
	
	/**
	 * Similar to {@code from(buf).apply(visitor)}, except this method analyzes the
	 * {@code buf} directly without any copy and extra operation. Thus it has
	 * higher performance. It's highly recommended to use this rather than invoke
	 * {@code from(buf)}.
	 */
	public static void apply(@Nonnull ByteBuf buf, @Nonnull DiffVisitor visitor) {
		
	}
}
