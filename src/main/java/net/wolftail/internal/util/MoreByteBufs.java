package net.wolftail.internal.util;

import java.io.IOException;

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
	
	public static PacketBuffer wrap(ByteBuf buf) {
		if (buf == null)
			return null;
		
		return buf instanceof PacketBuffer ? (PacketBuffer) buf : new PacketBuffer(buf);
	}
	
	public static ByteBuf unwrap(PacketBuffer buf) {
		if (buf == null)
			return null;
		
		ByteBuf r = buf.retain();
		r.release();
		
		return r;
	}
	
	public static int readVarInt(ByteBuf src) {
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
	
	public static <T extends ByteBuf> T writeVarInt(int i, T dst) {
		while ((i & -128) != 0) {
			dst.writeByte(i & 127 | 128);
			i >>>= 7;
		}
		
		dst.writeByte(i);
		
		return dst;
	}
	
	public static NBTTagCompound readTag(ByteBuf src) {
		try {
			return CompressedStreamTools.read(new ByteBufInputStream(src), NBTSizeTracker.INFINITE);
		} catch (IOException e) {
			throw new DecoderException(e);
		}
	}
	
	public static <T extends ByteBuf> T writeTag(NBTTagCompound tag, T dst) {
		try {
			CompressedStreamTools.write(tag, new ByteBufOutputStream(dst));
		} catch (IOException e) {
			throw new EncoderException(e);
		}
		
		return dst;
	}
}
