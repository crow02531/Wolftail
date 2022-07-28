package net.wolftail.util.tracker;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.DimensionType;
import net.minecraft.world.chunk.BlockStateContainer;
import net.wolftail.impl.util.MoreByteBuf;

final class ComplementaryCheckVisitor implements DiffVisitor {
	
	private boolean bind_world;
	private boolean bind_chunk;
	private boolean bind_block;
	
	@Override
	public void jzBegin() {
		
	}
	
	@Override
	public void jzEnd() {
		
	}
	
	@Override
	public void jzBindWorld(DimensionType dim) {
		bind_world = true;
		bind_chunk = false;
		bind_block = false;
	}
	
	@Override
	public void jzBindChunk(int chunkX, int chunkZ) {
		checkState(bind_world);
		checkArgument(-1875000 <= chunkX && chunkX < 1875000);
		checkArgument(-1875000 <= chunkZ && chunkZ < 1875000);
		
		bind_chunk = true;
		bind_block = false;
	}

	@Override
	public void jzBindBlock(short index) {
		checkState(bind_chunk);
		
		bind_block = true;
	}
	
	@Override
	public void jzUnbindWorld() {
		
	}
	
	@Override
	public void jzUnbindChunk() {
		
	}
	
	@Override
	public void jzUnbindBlock() {
		
	}
	
	@Override
	public void jzSetDaytime(long daytime) {
		checkState(bind_world);
	}
	
	@Override
	public void jzSetWeather(float rainStr, float thunderStr) {
		checkState(bind_world);
	}
	
	@Override
	public void jzSetSection(int index, ByteBuf buf) {
		checkState(bind_chunk);
		
		if(buf != null) {
			(new BlockStateContainer()).read(new PacketBuffer(buf));
			checkArgument(!buf.isReadable());
		}
	}
	
	@Override
	public void jzSetState(IBlockState state) {
		checkState(bind_block);
		checkNotNull(state);
	}
	
	@Override
	public void jzSetTileEntity(ByteBuf buf) {
		checkState(bind_block);
		
		if(buf != null) {
			MoreByteBuf.readTag(buf);
			checkArgument(!buf.isReadable());
		}
	}
}
