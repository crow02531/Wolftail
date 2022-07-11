package net.wolftail.util.tracker;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.impl.ImplCD;
import net.wolftail.impl.SharedImpls.H4;

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
		buf = buf.copy();
		
		return new ImplCD(ContentType.values()[H4.readVarInt(buf)].read(buf), buf.readerIndex(0).asReadOnly());
	}
	
	/**
	 * Similar to {@code from(buf).apply(dst)}, except this method analyze the buf
	 * directly without any copy.
	 */
	public static void apply(@Nonnull ByteBuf buf, @Nonnull SlaveUniverse dst) {
		PacketBuffer buf0 = buf instanceof PacketBuffer ? (PacketBuffer) buf : new PacketBuffer(buf);
		
		ContentType.values()[buf0.readVarInt()].apply(buf0, dst);
	}
}
