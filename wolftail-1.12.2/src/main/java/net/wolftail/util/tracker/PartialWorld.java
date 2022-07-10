package net.wolftail.util.tracker;

import javax.annotation.Nonnull;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;

public final class PartialWorld {
	
	private PartialUniverse universe;
	
	private final DimensionType dimension;
	
	final Long2ObjectMap<SlaveChunk> chunks;
	
	PartialWorld(PartialUniverse universeIn, DimensionType dimIn) {
		this.universe = universeIn;
		
		this.dimension = dimIn;
		
		this.chunks = new Long2ObjectOpenHashMap<>(8192);
	}
	
	@Nonnull
	public PartialUniverse universe() {
		return this.check().universe;
	}
	
	@Nonnull
	public DimensionType dimension() {
		return this.check().dimension;
	}
	
	public void free() {
		this.check().universe.worlds.remove(this.dimension);
		
		this.universe = null;
	}
	
	public boolean valid() {
		return this.universe != null;
	}
	
	private PartialWorld check() {
		if(!this.valid())
			throw new IllegalStateException();
		
		return this;
	}
	
	public SlaveChunk chunk(int chunkX, int chunkZ) {
		return this.check().chunks.get(ChunkPos.asLong(chunkX, chunkZ));
	}
	
	public IBlockState blockState(@Nonnull BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		
		if((y & 255) != y)
			throw new IllegalArgumentException();
		
		SlaveChunk c = this.check().chunk(x >> 4, z >> 4);
		
		return c == null ? null : c.get(x & 15, y, z & 15);
	}
}
