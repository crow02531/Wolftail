package net.wolftail.impl.tracker;

import java.io.IOException;

import org.apache.logging.log4j.core.util.Throwables;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;

public interface Insncodes {
	
	byte BIND_WORLD = 0,
			BIND_CHUNK = 1,
			BIND_BLOCK = 2;
	
	byte SET_DAYTIME = 3,
			SET_WEATHER = 4,
			SET_SECTION = 5,
			SET_STATE = 6,
			SET_TILEENTITY = 7;
	
	byte BAS_WORLD_DAYTIME = 8,
			BAS_WORLD_WEATHER = 9,
			BAS_BLOCK_STATE = 10,
			BAS_BLOCK_TILEENTITY = 11;
	
	byte BULK_BAS_BLOCK_STATE = 12,
			BULK_BAS_BLOCK_TILEENTITY = 13,
			BULK_SET_SECTION = 14;
	
	static int readVarInt(ByteBuf src) {
		int i = 0;
		int j = 0;
		
		while(true) {
			byte b0 = src.readByte();
			i |= (b0 & 127) << j++ * 7;
			
			if(j > 5)
				throw new DecoderException("VarInt too big");
			
			if((b0 & 128) != 128)
				break;
		}
		
		return i;
	}
	
	static void writeVarInt(int i, ByteBuf dst) {
		while((i & -128) != 0) {
			dst.writeByte(i & 127 | 128);
			i >>>= 7;
		}
		
		dst.writeByte(i);
	}
	
	static NBTTagCompound readTag(ByteBuf src) {
		try {
			return CompressedStreamTools.read(new ByteBufInputStream(src), NBTSizeTracker.INFINITE);
		} catch(IOException e) {
			Throwables.rethrow(e);
			
			return null;
		}
	}
	
	static void writeTag(NBTTagCompound src, ByteBuf dst) {
		try {
			CompressedStreamTools.write(src, new ByteBufOutputStream(dst));
		} catch(IOException e) {
			Throwables.rethrow(e);
		}
	}
}
