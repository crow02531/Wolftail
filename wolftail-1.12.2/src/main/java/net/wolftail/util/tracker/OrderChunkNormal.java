package net.wolftail.util.tracker;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import net.minecraft.world.DimensionType;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;

@Immutable
@SideWith(section = GameSection.GAME_PLAYING)
public final class OrderChunkNormal extends ContentOrder {
	
	private final ContentType type;
	
	final DimensionType dim;
	
	final int chunkX;
	final int chunkZ;
	
	OrderChunkNormal(ContentType type, DimensionType target, int chunkX, int chunkZ) {
		this.type = type;
		
		this.dim = target;
		
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
	}
	
	@Nonnull
	@Override
	public ContentType type() {
		return this.type;
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
		if(o == null || !(o instanceof OrderChunkNormal)) return false;
		
		OrderChunkNormal o0 = (OrderChunkNormal) o;
		
		return this.dim == o0.dim && this.chunkX == o0.chunkX && this.chunkZ == o0.chunkZ;
	}
	
	@Nonnull
	@Override
	public String toString() {
		return "CHUNK_BLOCK: " + this.dim + "[" + this.chunkX + ", " + this.chunkZ + "]";
	}
}
