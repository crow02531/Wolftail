package net.wolftail.util.tracker;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import net.minecraft.world.DimensionType;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;

/**
 * A kind of {@link ContentOrder}. Its type can only be either
 * {@link ContentType#WORLD_WEATHER WORLD_WEATHER} or
 * {@link ContentType#WORLD_DAYTIME WORLD_DAYTIME}. And the param is
 * {@link #dimension()}.
 * 
 * @see ContentOrder
 */
@Immutable
@SideWith(section = GameSection.GAME_PLAYING)
public final class OrderWorldNormal extends ContentOrder {
	
	private final ContentType type;
	
	final DimensionType dim;
	
	OrderWorldNormal(ContentType type, DimensionType dim) {
		this.type = type;
		
		this.dim = dim;
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
	
	@Override
	public int hashCode() {
		return this.dim.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(o == null || !(o instanceof OrderWorldNormal)) return false;
		
		return this.dim == ((OrderWorldNormal) o).dim;
	}
	
	@Nonnull
	@Override
	public String toString() {
		return this.type + ": " + this.dim;
	}
}