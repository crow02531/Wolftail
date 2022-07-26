package net.wolftail.util.tracker;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import io.netty.buffer.ByteBuf;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.impl.tracker.ImplCD;

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
	 * @param visitor	the diff visitor
	 */
	void apply(@Nonnull DiffVisitor visitor);
	
	int hashCode();
	
	boolean equals(Object o);
	
	/**
	 * Check if all readable bytes of {@code buf} make up a content diff. It
	 * will read all readable bytes.
	 * 
	 * @param buf	a non-null suspicious byte buf
	 * 
	 * @throws IllegalArgumentException	thrown when the check fails
	 */
	public static void check(@Nonnull ByteBuf buf) {
		try {
			ImplCD.apply(buf, new ComplementaryCheckVisitor());
		} catch(Throwable e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	/**
	 * Create a new content diff from the buf's all readable bytes. It will
	 * read all readable bytes.
	 * 
	 * @param buf	a buf whose all readable bytes composing one content diff
	 * 
	 * @return the content diff
	 */
	@Nonnull
	public static ContentDiff from(@Nonnull ByteBuf buf) {
		return new ImplCD(buf.readBytes(buf.readableBytes()).asReadOnly());
	}
	
	/**
	 * Similar to {@code from(buf).apply(visitor)}, except this method analyzes the
	 * {@code buf} directly without any copy. It will read all readable bytes.
	 * 
	 * @param buf		a buf whose all readable bytes composing one content diff
	 * @param visitor	the diff visitor
	 * 
	 * @see #apply(DiffVisitor)
	 */
	public static void apply(@Nonnull ByteBuf buf, @Nonnull DiffVisitor visitor) {
		ImplCD.apply(buf, visitor);
	}
}
