package net.wolftail.util.tracker.builtin;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import net.minecraft.world.DimensionType;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.util.tracker.ContentOrder;

@Immutable
@SideWith(section = GameSection.GAME_PLAYING)
public abstract class AbstractChunkOrder extends ContentOrder {
	
	@Nonnull
	protected final DimensionType dimension;
	
	protected final int chunkX;
	protected final int chunkZ;
	
	/**
	 * Constructs an abstract chunk order.
	 * 
	 * @param dim	the dimension
	 * @param x		chunkX, {@code -1875000 <= x < 1875000}
	 * @param z		chunkZ, {@code -1875000 <= z < 1875000}
	 */
	public AbstractChunkOrder(@Nonnull DimensionType dim, int x, int z) {
		this.dimension = dim;
		
		this.chunkX = x;
		this.chunkZ = z;
	}
	
	@Nonnull
	public final DimensionType getDimension() {
		return this.dimension;
	}
	
	public final int getChunkX() {
		return this.chunkX;
	}
	
	public final int getChunkZ() {
		return this.chunkZ;
	}
	
	@Override
	public final int hashCode() {
		return ((this.dimension.hashCode() + this.chunkX * 31) * 31 + this.chunkZ) ^ this.getClass().hashCode();
	}
	
	@Override
	public final boolean equals(Object o) {
		if(o == this) return true;
		if(o == null || o.getClass() != this.getClass()) return false;
		
		AbstractChunkOrder o0 = (AbstractChunkOrder) o;
		
		return this.dimension == o0.dimension && this.chunkX == o0.chunkX && this.chunkZ == o0.chunkZ;
	}
}
