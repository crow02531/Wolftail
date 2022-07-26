package net.wolftail.util.tracker;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.wolftail.util.ByteBufs;

final class ComplementaryCheckVisitor implements DiffVisitor {
	
	private boolean bind_world;
	private boolean bind_chunk;
	private boolean bind_section;
	private boolean bind_block;
	
	@Override
	public Object lockObject() {
		return this;
	}
	
	@Override
	public void jzBegin() {
		
	}
	
	@Override
	public void jzEnd() {
		
	}
	
	@Override
	public void jzBindWorld(ResourceKey<Level> dim) {
		bind_world = true;
		bind_chunk = false;
		bind_section = false;
		bind_block = false;
	}
	
	@Override
	public void jzBindChunk(int chunkX, int chunkZ) {
		checkState(bind_world);
		checkArgument(-1875000 <= chunkX && chunkX < 1875000);
		checkArgument(-1875000 <= chunkZ && chunkZ < 1875000);
		
		bind_chunk = true;
		bind_section = false;
		bind_block = false;
	}
	
	@Override
	public void jzBindSection(int index) {
		checkState(bind_chunk);
		
		bind_section = true;
		bind_block = false;
	}

	@Override
	public void jzBindBlock(short index) {
		checkState(bind_section);
		checkArgument((index & 0xF000) == 0);
		
		bind_block = true;
	}
	
	@Override
	public void jzUnbindWorld() {
		
	}
	
	@Override
	public void jzUnbindChunk() {
		
	}
	
	@Override
	public void jzUnbindSection() {
		
	}
	
	@Override
	public void jzUnbindBlock() {
		
	}
	
	@Override
	public void jzSetDaytime(long daytime) {
		checkState(bind_world);
	}
	
	@Override
	public void jzSetWeather(float rainLv, float thunderLv) {
		checkState(bind_world);
	}
	
	@Override
	public void jzSetSection(ByteBuf buf) {
		checkState(bind_section);
		
		if(buf != null) {
			(new PalettedContainer<BlockState>(Block.BLOCK_STATE_REGISTRY,
					Blocks.AIR.defaultBlockState(),
					PalettedContainer.Strategy.SECTION_STATES))
			.read(new FriendlyByteBuf(buf));
			
			checkArgument(!buf.isReadable());
		}
	}
	
	@Override
	public void jzSetState(BlockState state) {
		checkState(bind_block);
		checkNotNull(state);
	}
	
	@Override
	public void jzSetTileEntity(ByteBuf buf) {
		checkState(bind_block);
		
		if(buf != null) {
			ByteBufs.readTag(buf);
			
			checkArgument(!buf.isReadable());
		}
	}
}
