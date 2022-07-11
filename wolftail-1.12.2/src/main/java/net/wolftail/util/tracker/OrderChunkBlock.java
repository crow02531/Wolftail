package net.wolftail.util.tracker;

import javax.annotation.Nonnull;

import net.minecraft.world.DimensionType;

public final class OrderChunkBlock extends ContentOrder {
	
	final DimensionType dim;
	
	final int chunkX;
	final int chunkZ;
	
	OrderChunkBlock(DimensionType target, int chunkX, int chunkZ) {
		this.dim = target;
		
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
	}
	
	@Nonnull
	@Override
	public ContentType type() {
		return ContentType.CHUNK_BLOCK;
	}
	
	@Nonnull
	public DimensionType dimension() {
		return this.dim;
	}
	
	public int chunkX() {
		return this.chunkX;
	}
	
	public int chunkZ() {
		return this.chunkZ;
	}
	
	@Override
	public int hashCode() {
		return (this.dim.hashCode() + this.chunkX * 31) * 31 + this.chunkZ;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || o.getClass() != OrderChunkBlock.class) return false;
		
		OrderChunkBlock o0 = (OrderChunkBlock) o;
		
		return this.dim == o0.dim && this.chunkX == o0.chunkX && this.chunkZ == o0.chunkZ;
	}
	
	@Nonnull
	@Override
	public String toString() {
		return "CHUNK_BLOCK: " + this.dim + "[" + this.chunkX + ", " + this.chunkZ + "]";
	}
}
