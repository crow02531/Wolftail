package net.wolftail.util;

import java.io.IOException;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

public final class MoreByteBufs {
	
	private MoreByteBufs() {
	}
	
	/**
	 * Wrap the given byte buf, or return it directly if already wrapped.
	 * 
	 * @param buf the byte buf to be wrapped
	 * 
	 * @return the 'friendly byte buf', null only if {@code buf} is null
	 */
	public static PacketBuffer wrap(ByteBuf buf) {
		if (buf == null)
			return null;
		
		return buf instanceof PacketBuffer ? (PacketBuffer) buf : new PacketBuffer(buf);
	}
	
	/**
	 * Return the underlying buffer instance of the packet buffer.
	 * 
	 * @param buf the packet buffer
	 * 
	 * @return the underlying byte buf, null only if {@code buf} is null
	 */
	public static ByteBuf unwrap(PacketBuffer buf) {
		return buf == null ? null : buf.skipBytes(0);
	}
	
	/**
	 * Read a var int from {@code src}.
	 * 
	 * @param src the source
	 * 
	 * @return the written integer
	 */
	public static int readVarInt(@Nonnull ByteBuf src) {
		int i = 0;
		int j = 0;
		
		while (true) {
			byte b0 = src.readByte();
			i |= (b0 & 127) << j++ * 7;
			
			if (j > 5)
				throw new DecoderException("VarInt too big");
			
			if ((b0 & 128) != 128)
				break;
		}
		
		return i;
	}
	
	/**
	 * Read a compound tag from {@code src}. In a uncompressed form.
	 * 
	 * @param src the source
	 * 
	 * @return the written compound tag
	 */
	@Nonnull
	public static NBTTagCompound readTag(@Nonnull ByteBuf src) {
		try {
			return CompressedStreamTools.read(new ByteBufInputStream(src), NBTSizeTracker.INFINITE);
		} catch (IOException e) {
			throw new DecoderException(e);
		}
	}
	
	/**
	 * Write a var int to {@code dst}.
	 * 
	 * @param i   the integer
	 * @param dst the destination
	 * 
	 * @return {@code dst}
	 */
	public static <T extends ByteBuf> T writeVarInt(int i, @Nonnull T dst) {
		while ((i & -128) != 0) {
			dst.writeByte(i & 127 | 128);
			i >>>= 7;
		}
		
		dst.writeByte(i);
		
		return dst;
	}
	
	/**
	 * Write a compound tag to {@code dst}. In a uncompressed form.
	 * 
	 * @param tag the compound tag
	 * @param dst the destination
	 * 
	 * @return {@code dst}
	 */
	@Nonnull
	public static <T extends ByteBuf> T writeTag(@Nonnull NBTTagCompound tag, @Nonnull T dst) {
		try {
			CompressedStreamTools.write(tag, new ByteBufOutputStream(dst));
		} catch (IOException e) {
			throw new EncoderException(e);
		}
		
		return dst;
	}
}
