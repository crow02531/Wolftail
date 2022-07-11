package net.wolftail.util.tracker;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.BlockStateContainer;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;

@SideWith(section = GameSection.GAME_PLAYING)
public final class SlaveChunk {
	
	private SlaveWorld world;
	
	private final int chunkX;
	private final int chunkZ;
	
	final BlockStateContainer[] blocks;
	
	SlaveChunk(SlaveWorld worldIn, int x, int z) {
		this.world = worldIn;
		
		this.chunkX = x;
		this.chunkZ = z;
		
		this.blocks = new BlockStateContainer[16];
	}
	
	@Nonnull
	public SlaveWorld world() {
		return this.check().world;
	}
	
	public int chunkX() {
		return this.check().chunkX;
	}
	
	public int chunkZ() {
		return this.check().chunkZ;
	}
	
	public void release() {
		this.check().world.chunks.remove(ChunkPos.asLong(this.chunkX, this.chunkZ));
		
		this.world = null;
	}
	
	public boolean valid() {
		return this.world != null;
	}
	
	private SlaveChunk check() {
		if(!this.valid())
			throw new IllegalStateException();
		
		return this;
	}
	
	@Nonnull
	public IBlockState blockState(int localX, int localY, int localZ) {
		if((localX & 15) != localX || (localZ & 15) != localZ || (localY & 255) != localY)
			throw new IllegalArgumentException();
		
		return this.check().get(localX, localY, localZ);
	}
	
	IBlockState get(int lx, int ly, int lz) {
		BlockStateContainer c = this.blocks[ly >> 4];
		
		return c == null ? Blocks.AIR.getDefaultState() : c.get(lx, ly & 15, lz);
	}
	
	void set(short index, IBlockState state) {
		int i = (index & 255) >> 4;
		
		if(this.blocks[i] == null)
			this.blocks[i] = new BlockStateContainer();
		
		this.blocks[i].set(index >> 12 & 15, index & 15, index >> 8 & 15, state);
	}
}
