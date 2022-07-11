package net.wolftail.util.tracker;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.impl.ImplCD;
import net.wolftail.impl.SharedImpls;

@Immutable
@SideWith(section = GameSection.GAME_PLAYING)
public interface ContentDiff {
	
	@Nonnull ContentOrder order();
	
	/**
	 * Transfer the whole content diff into a newly created
	 * byte buf. Don't worry about its performance.
	 * 
	 * @return the byte buf
	 */
	@Nonnull ByteBuf toByteBuf();
	
	/**
	 * Apply the content diff to the given partial universe.
	 * 
	 * @see #apply(ByteBuf, PartialUniverse)
	 */
	void apply(@Nonnull PartialUniverse dst);
	
	int hashCode();
	
	boolean equals(Object o);
	
	/**
	 * Create a new content diff from the buf's all readable bytes.
	 * 
	 * @return the content diff
	 */
	@Nonnull
	public static ContentDiff from(@Nonnull ByteBuf buf) {
		buf = buf.copy();
		
		buf.readByte();
		
		return new ImplCD(SharedImpls.H2.readOrder(buf), buf.readerIndex(0).asReadOnly());
	}
	
	/**
	 * Similar to {@code from(buf).apply(dst)}, except this method analyze the buf
	 * directly without any copy.
	 */
	public static void apply(@Nonnull ByteBuf buf, @Nonnull PartialUniverse dst) {
		dst.accept(buf instanceof PacketBuffer ? (PacketBuffer) buf : new PacketBuffer(buf));
	}
}
