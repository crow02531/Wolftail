package net.wolftail.util.tracker;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.impl.tracker.ImplCD;

/**
 * Represents a change of context or the content itself. A content
 * diff is essentially a list of instructions(or instruction array).
 * You can use a diff visitor to visit these instructions.
 * 
 * @see DiffVisitor
 */
@Immutable
@SideWith(section = GameSection.GAME_PLAYING)
public interface ContentDiff {
	
	/**
	 * Transfers the whole content diff into a newly created
	 * read-only byte buf. Don't worry about its performance.
	 * 
	 * @return a newly created read-only buf
	 */
	@Nonnull ByteBuf toByteBuf();
	
	/**
	 * Makes the given visitor visit this content diff.
	 * 
	 * <p>
	 * The current thread will read instruction in this content
	 * diff one by one and visit jz* methods in {@code visitor}.
	 * </p>
	 * 
	 * @param visitor	the diff visitor
	 */
	void apply(@Nonnull DiffVisitor visitor);
	
	/**
	 * Returns a hash code based on the instruction array of the
	 * content diff.
	 */
	int hashCode();
	
	/**
	 * Determines if the instruction array of the given content diff is
	 * identical to this content diff's.
	 * 
	 * <p>
	 * The 'identical' here only means two instruction array has the same
	 * size and every single instruction of the two arrays are the same.
	 * </p>
	 */
	boolean equals(Object o);
	
	/**
	 * Check if all readable bytes of {@code buf} make up a content diff.
	 * It will read all readable bytes.
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
		return new ImplCD(Unpooled.copiedBuffer(buf).asReadOnly());
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
