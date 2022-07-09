package net.wolftail.util.tracker;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.chunk.BlockStateContainer;

public final class SlaveChunk {
	
	private final PartialWorld world;
	
	private final int chunkX;
	private final int chunkZ;
	
	final BlockStateContainer[] blocks;
	
	SlaveChunk(PartialWorld worldIn, int x, int z) {
		this.world = worldIn;
		
		this.chunkX = x;
		this.chunkZ = z;
		
		BlockStateContainer[] b = this.blocks = new BlockStateContainer[16];
		for(int i = 0; i < 16; ++i)
			b[i] = new BlockStateContainer();
	}
	
	@Nonnull
	public PartialWorld world() {
		return this.world;
	}
	
	public int chunkX() {
		return this.chunkX;
	}
	
	public int chunkZ() {
		return this.chunkZ;
	}
	
	@Nonnull
	public IBlockState blockState(int localX, int localY, int localZ) {
		return this.blocks[localY >> 4].get(localX, localY & 15, localZ);
	}
	
	void set(short index, IBlockState state) {
		//TODO
	}
}
