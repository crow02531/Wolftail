package net.wolftail.util.tracker;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.impl.ImplCD;
import net.wolftail.impl.SharedImpls;

@SideWith(section = GameSection.GAME_PLAYING)
public interface ContentDiff {
	
	@Nonnull SubscribeOrder order();
	
	@Nonnull ByteBuf asByteBuf();
	
	void apply(@Nonnull PartialUniverse dst);
	
	int hashCode();
	
	boolean equals(Object o);
	
	@Nonnull
	public static ContentDiff asContentDiff(@Nonnull ByteBuf buf) {
		buf = buf.copy();
		
		buf.readByte();
		
		return new ImplCD(SharedImpls.H2.readOrder(buf), buf.readerIndex(0).asReadOnly());
	}
	
	public static void apply(@Nonnull ByteBuf buf, @Nonnull PartialUniverse dst) {
		dst.accept(buf instanceof PacketBuffer ? (PacketBuffer) buf : new PacketBuffer(buf));
	}
}
