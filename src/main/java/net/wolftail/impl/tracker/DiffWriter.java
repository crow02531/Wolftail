package net.wolftail.impl.tracker;

import static net.wolftail.util.ByteBufs.writeResourceLocation;
import static net.wolftail.util.ByteBufs.writeVarInt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.wolftail.util.tracker.ContentDiff;
import net.wolftail.util.tracker.DiffVisitor;

//TODO make DiffWriter able to produce BAS and BULK codes
public final class DiffWriter implements DiffVisitor, Insncodes {
	
	private ByteBuf buf;
	
	private ResourceKey<Level> bind_world;
	private int[] bind_chunk; //length=2
	private int bind_section; //128 means unbind
	private int	bind_block; //-1 means unbind
	
	@Override
	public Object lockObject() {
		return this;
	}
	
	@Override
	public void jzBegin() {
		if(buf == null)
			buf = Unpooled.buffer();
	}
	
	@Override
	public void jzEnd() {
		//NOOP
	}
	
	public ContentDiff harvest() {
		if(buf != null) {
			ImplCD r = new ImplCD(buf.asReadOnly());
			
			buf = null;
			bind_world = null;
			bind_chunk = null;
			bind_section = 128;
			bind_block = -1;
			
			return r;
		}
		
		return null;
	}
	
	@Override
	public void jzBindWorld(ResourceKey<Level> dim) {
		bind_chunk = null;
		bind_section = 128;
		bind_block = -1;
		
		if(bind_world != dim) {
			bind_world = dim;
			
			buf.writeByte(BIND_WORLD);
			writeResourceLocation(dim.location(), buf);
		}
	}
	
	@Override
	public void jzBindChunk(int chunkX, int chunkZ) {
		bind_section = 128;
		bind_block = -1;
		
		int[] b = bind_chunk;
		
		if(b == null || b[0] != chunkX || b[1] != chunkZ) {
			if(b == null) bind_chunk = b = new int[2];
			
			buf.writeByte(BIND_CHUNK);
			writeChunkPos(b[0] = chunkX, b[1] = chunkZ, buf);
		}
	}
	
	@Override
	public void jzBindSection(int index) {
		bind_block = -1;
		
		if(bind_section != index) {
			bind_section = index;
			
			buf.writeByte(BIND_SECTION);
			writeVarInt(index, buf);
		}
	}
	
	@Override
	public void jzBindBlock(short index) {
		int i = Short.toUnsignedInt(index);
		
		if(bind_block != i) {
			bind_block = i;
			
			buf.writeByte(BIND_BLOCK);
			buf.writeShort(i);
		}
	}
	
	@Override
	public void jzUnbindWorld() {
		//NOOP
	}
	
	@Override
	public void jzUnbindChunk() {
		//NOOP
	}
	
	@Override
	public void jzUnbindSection() {
		//NOOP
	}
	
	@Override
	public void jzUnbindBlock() {
		//NOOP
	}
	
	@Override
	public void jzSetDaytime(long daytime) {
		buf.writeByte(SET_DAYTIME);
		buf.writeLong(daytime);
	}
	
	@Override
	public void jzSetWeather(float rainLv, float thunderLv) {
		buf.writeByte(SET_WEATHER);
		buf.writeFloat(rainLv);
		buf.writeFloat(thunderLv);
	}
	
	@Override
	public void jzSetSection(ByteBuf src) {
		buf.writeByte(SET_SECTION);
		writeNullOrNonEmptyBuf(src, buf);
	}
	
	@Override
	public void jzSetState(BlockState state) {
		buf.writeByte(SET_STATE);
		writeVarInt(Block.getId(state), buf);
	}
	
	@Override
	public void jzSetTileEntity(ByteBuf src) {
		buf.writeByte(SET_TILEENTITY);
		writeNullOrNonEmptyBuf(src, buf);
	}
	
	private static void writeNullOrNonEmptyBuf(ByteBuf src, ByteBuf dst) {
		if(src == null)
			dst.writeByte(0); //the varint 0
		else {
			writeVarInt(src.readableBytes(), dst); //readableBytes can't be 0
			dst.writeBytes(src);
		}
	}
	
	private static void writeChunkPos(int x, int z, ByteBuf dst) {
		x += 1875000;
		z += 1875000;
		
		dst.writeShort(x);
		dst.writeShort(z & 0x3FF | x >> 16);
		dst.writeShort(z >> 10);
	}
}
