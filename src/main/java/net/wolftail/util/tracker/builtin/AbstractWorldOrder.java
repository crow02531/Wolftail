package net.wolftail.util.tracker.builtin;

import javax.annotation.Nonnull;

import net.minecraft.world.DimensionType;
import net.wolftail.util.tracker.ContentOrder;

public abstract class AbstractWorldOrder extends ContentOrder {
	
	@Nonnull
	protected final DimensionType dimension;
	
	public AbstractWorldOrder(@Nonnull DimensionType dim) {
		this.dimension = dim;
	}
	
	@Nonnull
	public final DimensionType getDimension() {
		return this.dimension;
	}
	
	@Override
	public final int hashCode() {
		return this.dimension.hashCode() ^ this.getClass().hashCode();
	}
	
	@Override
	public final boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null || o.getClass() != this.getClass())
			return false;
		
		return this.dimension == ((AbstractWorldOrder) o).dimension;
	}
}
