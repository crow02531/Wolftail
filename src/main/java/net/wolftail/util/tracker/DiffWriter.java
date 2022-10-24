package net.wolftail.util.tracker;

import static net.wolftail.util.MoreByteBufs.writeVarInt;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.DimensionType;
import net.wolftail.internal.tracker.Insncodes;

/**
 * A special diff visitor that could record instructions and use it to produce
 * content diff.
 * 
 * @see ContentDiff
 * @see DiffVisitor
 */
public final class DiffWriter implements DiffVisitor, Insncodes {
	// TODO make DiffWriter able to produce BAS and BULK codes
	
	private ByteBuf buffer;
	
	private DimensionType bind_world;
	private int[] bind_chunk; // length=2
	private int bind_block = -1; // -1 means unbind
	
	/**
	 * Set the output buffer.
	 * 
	 * <p>
	 * This method shouldn't be invoked during visiting process.
	 * </p>
	 * 
	 * @param buf the new output buffer
	 */
	public void setOutput(ByteBuf buf) {
		this.buffer = buf;
		
		this.bind_world = null;
		this.bind_chunk = null;
		this.bind_block = -1;
	}
	
	@Override
	public void jzBegin() {
		// NOOP
	}
	
	@Override
	public void jzEnd() {
		// NOOP
	}
	
	@Override
	public void jzBindWorld(DimensionType dim) {
		bind_chunk = null;
		bind_block = -1;
		
		if (bind_world != dim) {
			bind_world = dim;
			
			buffer.writeByte(BIND_WORLD);
			writeVarInt(dim.getId(), buffer);
		}
	}
	
	@Override
	public void jzBindChunk(int chunkX, int chunkZ) {
		bind_block = -1;
		
		int[] b = bind_chunk;
		
		if (b == null || b[0] != chunkX || b[1] != chunkZ) {
			if (b == null)
				bind_chunk = b = new int[2];
			
			buffer.writeByte(BIND_CHUNK);
			write_chunkpos(b[0] = chunkX, b[1] = chunkZ, buffer);
		}
	}
	
	@Override
	public void jzBindBlock(short index) {
		int i = Short.toUnsignedInt(index);
		
		if (bind_block != i) {
			bind_block = i;
			
			buffer.writeByte(BIND_BLOCK);
			buffer.writeShort(i);
		}
	}
	
	@Override
	public void jzUnbindWorld() {
		// NOOP
	}
	
	@Override
	public void jzUnbindChunk() {
		// NOOP
	}
	
	@Override
	public void jzUnbindBlock() {
		// NOOP
	}
	
	@Override
	public void jzSetDaytime(int daytime) {
		buffer.writeByte(SET_DAYTIME);
		buffer.writeShort(daytime);
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
		
		if (buf == null)
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
		write_state(state, buffer);
	}
	
	@Override
	public void jzSetTileEntity(ByteBuf buf) {
		buffer.writeByte(SET_TILEENTITY);
		
		if (buf == null)
			buffer.writeByte(0); // the varint 0
		else {
			writeVarInt(buf.readableBytes(), buffer); // readableBytes can't be 0
			buffer.writeBytes(buf);
		}
	}
	
	private static void write_chunkpos(int x, int z, ByteBuf dst) {
		x += 1875000;
		z += 1875000;
		
		dst.writeShort(x);
		dst.writeShort(z & 0x3FF | x >> 16);
		dst.writeShort(z >> 10);
	}
	
	@SuppressWarnings("deprecation")
	private static void write_state(IBlockState state, ByteBuf dst) {
		writeVarInt(Block.BLOCK_STATE_IDS.get(state), dst);
	}
}
