package net.wolftail.util.tracker;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.wolftail.api.lifecycle.Sealed;
import net.wolftail.internal.tracker.ImplCD;

/**
 * Represents a change of content or the content itself. A content diff is
 * essentially a list of instructions(or instruction array). You can use a diff
 * visitor to visit these instructions.
 * 
 * @see DiffVisitor
 */
@Sealed
@Immutable
public interface ContentDiff {
	
	/**
	 * Makes the given visitor visit this content diff.
	 * 
	 * <p>
	 * The current thread will read all instructions in this content diff one by one
	 * and visit corresponding {@code jz*} methods in {@code visitor}. A {@code jz*}
	 * invoke(including {@code jzBegin} and {@code jzEnd}) corresponds to an
	 * instruction in the diff.
	 * </p>
	 * 
	 * @param visitor the diff visitor
	 */
	void apply(@Nonnull DiffVisitor visitor);
	
	/**
	 * Transfers the whole content diff into {@code dst}.
	 * 
	 * <p>
	 * The written data is a 'compressed version' of the content diff. If two
	 * content diffs are equal, then their 'compressed version' are also equal.
	 * </p>
	 * 
	 * @param dst the buffer to be written
	 */
	void to(@Nonnull ByteBuf dst);
	
	/**
	 * Returns a hash code based on the instruction array of the content diff.
	 */
	int hashCode();
	
	/**
	 * Determines if the instruction array of the given content diff is identical to
	 * this content diff's.
	 * 
	 * <p>
	 * The 'identical' here only means two instruction array has the same size and
	 * every single instruction of the two arrays are the same.
	 * </p>
	 */
	boolean equals(Object o);
	
	/**
	 * Creates a new content diff from the buf's all readable bytes. It will read
	 * all readable bytes.
	 * 
	 * @param buf a buf whose all readable bytes composing one content diff
	 * 
	 * @return the content diff
	 */
	@Nonnull
	static ContentDiff of(@Nonnull ByteBuf buf) {
		return new ImplCD(ByteBufUtil.getBytes(buf));
	}
	
	/**
	 * Similar to {@code of(buf).apply(visitor)}, except this method analyzes the
	 * {@code buf} directly without any copy. It will read all readable bytes.
	 * 
	 * @param buf     a buf whose all readable bytes composing one content diff
	 * @param visitor the diff visitor
	 * 
	 * @see #apply(DiffVisitor)
	 */
	static void apply(@Nonnull ByteBuf buf, @Nonnull DiffVisitor visitor) {
		ImplCD.apply(buf, visitor);
	}
	
	/**
	 * Check if all readable bytes of {@code buf} make up a content diff.
	 * 
	 * @param buf a suspicious byte buf
	 * 
	 * @throws IllegalArgumentException thrown when the check fails
	 */
	static void check(@Nonnull ByteBuf buf) {
		ImplCD.check(buf);
	}
}
