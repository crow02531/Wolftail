package net.wolftail.util.tracker;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import net.minecraft.world.DimensionType;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;

@Immutable
@SideWith(section = GameSection.GAME_PLAYING)
public final class OrderWorldWeather extends ContentOrder {
	
	final DimensionType dim;
	
	OrderWorldWeather(DimensionType dim) {
		this.dim = dim;
	}
	
	@Nonnull
	@Override
	public ContentType type() {
		return ContentType.WORLD_WEATHER;
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
		if(o == null || !(o instanceof OrderWorldWeather)) return false;
		
		return this.dim == ((OrderWorldWeather) o).dim;
	}
}
