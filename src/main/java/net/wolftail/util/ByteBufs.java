package net.wolftail.util;

import java.io.IOException;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.ResourceLocationException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public final class ByteBufs {
	
	private ByteBufs() {}
	
	public static FriendlyByteBuf wrap(ByteBuf buf) {
		if(buf == null) return null;
		
		return buf instanceof FriendlyByteBuf ? (FriendlyByteBuf) buf : new FriendlyByteBuf(buf);
	}
	
	public static ByteBuf unwrap(FriendlyByteBuf buf) {
		if(buf == null) return null;
		
		ByteBuf r = buf.retain();
		buf.release();
		
		return r;
	}
	
	public static int readVarInt(@Nonnull ByteBuf src) {
		byte b;
		int i = 0;
		int j = 0;
		
		do {
			b = src.readByte();
			i |= (b & 0x7F) << j++ * 7;
			
			if (j <= 5) continue;
			throw new DecoderException("VarInt too big");
		} while((b & 0x80) == 128);
		
		return i;
	}
	
	public static void writeVarInt(int i, @Nonnull ByteBuf dst) {
		while((i & -128) != 0) {
			dst.writeByte(i & 127 | 128);
			i >>>= 7;
		}
		
		dst.writeByte(i);
	}
	
	@Nonnull
	public static CompoundTag readTag(@Nonnull ByteBuf src) {
		try {
			return NbtIo.read(new ByteBufInputStream(src));
		} catch(IOException e) {
			throw new DecoderException(e);
		}
	}
	
	public static void writeTag(@Nonnull CompoundTag t, @Nonnull ByteBuf dst) {
		try {
			NbtIo.write(t, new ByteBufOutputStream(dst));
		} catch(IOException e) {
			throw new EncoderException(e);
		}
	}
	
	@Nonnull
	public static String readUtf(@Nonnull ByteBuf src) {
		try(ByteBufInputStream is = new ByteBufInputStream(src)) {
			return is.readUTF();
		} catch(IOException e) {
			throw new DecoderException(e);
		}
	}
	
	public static void writeUtf(@Nonnull String s, @Nonnull ByteBuf dst) {
		try(ByteBufOutputStream os = new ByteBufOutputStream(dst)) {
			os.writeUTF(s);
		} catch(IOException e) {
			throw new EncoderException(e);
		}
	}
	
	@Nonnull
	public static ResourceLocation readResourceLocation(@Nonnull ByteBuf src) {
		try {
			return new ResourceLocation(readUtf(src));
		} catch(ResourceLocationException e) {
			throw new DecoderException(e);
		}
	}
	
	public static void writeResourceLocation(@Nonnull ResourceLocation rl, @Nonnull ByteBuf dst) {
		writeUtf(rl.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE) ? rl.getPath() : rl.toString(), dst);
	}
}
