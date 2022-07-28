package net.wolftail.util.tracker;

import static net.wolftail.impl.util.MoreByteBuf.writeVarInt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.DimensionType;
import net.wolftail.impl.tracker.ImplCD;
import net.wolftail.impl.tracker.Insncodes;

/**
 * A special diff visitor that could record instructions and use it
 * to produce content diffs.
 * 
 * @see ContentDiff
 * @see DiffVisitor
 */
public final class DiffWriter implements DiffVisitor, Insncodes {
	//TODO make DiffWriter able to produce BAS and BULK codes
	
	private ByteBuf buffer;
	
	private DimensionType	bind_world;
	private int[]			bind_chunk; //length=2
	private int				bind_block; //-1 means unbind
	
	/**
	 * Produce a content diff based on the instructions
	 * recorded between previous call and now.
	 * 
	 * <p>
	 * Only a single thread is allowed to invoke this method
	 * at a time.
	 * </p>
	 * 
	 * @return the produced content diff, null indicates no
	 * 		instructions recorded
	 */
	public ContentDiff harvest() {
		if(buffer != null) {
			ImplCD r = new ImplCD(buffer.asReadOnly());
			
			buffer = null;
			
			bind_world = null;
			bind_chunk = null;
			bind_block = -1;
			
			return r;
		}
		
		return null;
	}
	
	@Override
	public void jzBegin() {
		if(buffer == null)
			buffer = Unpooled.buffer();
	}
	
	@Override
	public void jzEnd() {
		//NOOP
	}
	
	@Override
	public void jzBindWorld(DimensionType dim) {
		bind_chunk = null;
		bind_block = -1;
		
		if(bind_world != dim) {
			bind_world = dim;
			
			buffer.writeByte(BIND_WORLD);
			writeVarInt(dim.getId(), buffer);
		}
	}
	
	@Override
	public void jzBindChunk(int chunkX, int chunkZ) {
		bind_block = -1;
		
		int[] b = bind_chunk;
		
		if(b == null || b[0] != chunkX || b[1] != chunkZ) {
			if(b == null) bind_chunk = b = new int[2];
			
			buffer.writeByte(BIND_CHUNK);
			writeChunkPos(b[0] = chunkX, b[1] = chunkZ, buffer);
		}
	}
	
	@Override
	public void jzBindBlock(short index) {
		int i = Short.toUnsignedInt(index);
		
		if(bind_block != i) {
			bind_block = i;
			
			buffer.writeByte(BIND_BLOCK);
			buffer.writeShort(i);
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
	public void jzUnbindBlock() {
		//NOOP
	}
	
	@Override
	public void jzSetDaytime(long daytime) {
		buffer.writeByte(SET_DAYTIME);
		buffer.writeLong(daytime);
	}
	
	@Override
	public void jzSetWeather(float rainStr, float thunderStr) {
		buffer.writeByte(SET_WEATHER);
		buffer.writeFloat(rainStr);
		buffer.writeFloat(thunderStr);
	}
	
	@Override
	public void jzSetSection(int index, ByteBuf buf) {
		buffer.writeByte(SET_SECTION);
		
		if(buf == null)
			buffer.writeByte(index);
		else {
			buffer.writeByte(0x10 | index);
			writeVarInt(buf.readableBytes(), buffer);
			buffer.writeBytes(buf);
		}
	}
	
	@Override
	public void jzSetState(IBlockState state) {
		buffer.writeByte(SET_STATE);
		writeBlockState(state, buffer);
	}
	
	@Override
	public void jzSetTileEntity(ByteBuf buf) {
		buffer.writeByte(SET_TILEENTITY);
		
		if(buf == null)
			buffer.writeByte(0); //the varint 0
		else {
			writeVarInt(buf.readableBytes(), buffer); //readableBytes can't be 0
			buffer.writeBytes(buf);
		}
	}
	
	private static void writeChunkPos(int x, int z, ByteBuf dst) {
		x += 1875000;
		z += 1875000;
		
		dst.writeShort(x);
		dst.writeShort(z & 0x3FF | x >> 16);
		dst.writeShort(z >> 10);
	}
	
	@SuppressWarnings("deprecation")
	private static void writeBlockState(IBlockState state, ByteBuf dst) {
		writeVarInt(Block.BLOCK_STATE_IDS.get(state), dst);
	}
}
