package net.wolftail.util.tracker;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import net.minecraft.world.DimensionType;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;

@Immutable
@SideWith(section = GameSection.GAME_PLAYING)
public final class SubscribeOrder {
	
	private DimensionType dim;
	
	private int chunkX;
	private int chunkZ;
	
	public SubscribeOrder(@Nonnull DimensionType target, int chunkX, int chunkZ) {
		this.dim = Objects.requireNonNull(target);
		
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
	}
	
	@Nonnull
	public DimensionType target() {
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
		if(o == null || o.getClass() != SubscribeOrder.class) return false;
		
		SubscribeOrder other = (SubscribeOrder) o;
		
		return this.dim == other.dim && this.chunkX == other.chunkX && this.chunkZ == other.chunkZ;
	}
	
	@Override
	public String toString() {
		return this.dim + "[" + this.chunkX + ", " + this.chunkZ + "]";
	}
}
