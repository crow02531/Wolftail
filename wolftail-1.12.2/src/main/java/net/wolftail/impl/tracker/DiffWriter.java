package net.wolftail.impl.tracker;

import static net.wolftail.impl.tracker.Insncodes.writeTag;
import static net.wolftail.impl.tracker.Insncodes.writeVarInt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.DimensionType;
import net.minecraft.world.chunk.BlockStateContainer;
import net.wolftail.util.tracker.ContentDiff;
import net.wolftail.util.tracker.DiffVisitor;

//TODO make DiffWriter able to produce BAS and BULK codes
public final class DiffWriter implements DiffVisitor, Insncodes {
	
	private ByteBuf buf;
	
	private DimensionType	bind_world;
	private int[]			bind_chunk; //length=2
	private int				bind_block; //-1 means unbind
	
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
			bind_block = -1;
			
			return r;
		}
		
		return null;
	}
	
	@Override
	public void jzBindWorld(DimensionType dim) {
		bind_chunk = null;
		bind_block = -1;
		
		if(bind_world != dim) {
			bind_world = dim;
			
			buf.writeByte(BIND_WORLD);
			writeVarInt(dim.getId(), buf);
		}
	}
	
	@Override
	public void jzBindChunk(int chunkX, int chunkZ) {
		bind_block = -1;
		
		int[] b = bind_chunk;
		
		if(b == null || b[0] != chunkX || b[1] != chunkZ) {
			if(b == null) bind_chunk = b = new int[2];
			
			buf.writeByte(BIND_CHUNK);
			writeChunkPos(b[0] = chunkX, b[1] = chunkZ, buf);
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
		bind_world = null;
		bind_chunk = null;
		bind_block = -1;
	}
	
	@Override
	public void jzUnbindChunk() {
		bind_chunk = null;
		bind_block = -1;
	}
	
	@Override
	public void jzUnbindBlock() {
		bind_block = -1;
	}
	
	@Override
	public void jzSetDaytime(long daytime) {
		buf.writeByte(SET_DAYTIME);
		buf.writeLong(daytime);
	}
	
	@Override
	public void jzSetWeather(float rainStr, float thunderStr) {
		buf.writeByte(SET_WEATHER);
		buf.writeFloat(rainStr);
		buf.writeFloat(thunderStr);
	}
	
	@Override
	public void jzSetSection(int index, BlockStateContainer blockStateLayer) {
		buf.writeByte(SET_SECTION);
		
		if(blockStateLayer == null)
			buf.writeByte(index);
		else {
			buf.writeByte(0x10 | index);
			blockStateLayer.write(new PacketBuffer(buf));
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void jzSetState(IBlockState state) {
		buf.writeByte(SET_STATE);
		writeVarInt(Block.BLOCK_STATE_IDS.get(state), buf);
	}
	
	@Override
	public void jzSetTileEntity(NBTTagCompound serialized) {
		buf.writeByte(SET_TILEENTITY);
		
		if(serialized == null)
			buf.writeBoolean(false);
		else {
			buf.writeBoolean(true);
			writeTag(serialized, buf);
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
