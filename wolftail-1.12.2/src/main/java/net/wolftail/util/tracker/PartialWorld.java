package net.wolftail.util.tracker;

import javax.annotation.Nonnull;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;

public final class PartialWorld {
	
	private final PartialUniverse universe;
	
	private final DimensionType dimension;
	
	final Long2ObjectMap<SlaveChunk> chunks;
	
	PartialWorld(PartialUniverse universeIn, DimensionType dimIn) {
		this.universe = universeIn;
		
		this.dimension = dimIn;
		
		this.chunks = new Long2ObjectOpenHashMap<>(8192);
	}
	
	@Nonnull
	public PartialUniverse universe() {
		return this.universe;
	}
	
	@Nonnull
	public DimensionType dimension() {
		return this.dimension;
	}
	
	public SlaveChunk chunk(int chunkX, int chunkZ) {
		return this.chunks.get(ChunkPos.asLong(chunkX, chunkZ));
	}
	
	public IBlockState blockState(@Nonnull BlockPos pos) {
		SlaveChunk c = this.chunk(pos.getX() >> 4, pos.getZ() >> 4);
		
		return c == null ? null : c.blockState(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
	}
}
