package net.wolftail.util.tracker.builtin;

import javax.annotation.Nonnull;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.wolftail.util.tracker.ContentOrder;

public abstract class AbstractBlockOrder extends ContentOrder {
	
	@Nonnull
	protected final DimensionType dimension;
	
	@Nonnull
	protected final BlockPos position;
	
	/**
	 * Constructs an abstract block order.
	 * 
	 * @param dim the dimension
	 * @param pos the block position, must within buildable area
	 */
	public AbstractBlockOrder(@Nonnull DimensionType dim, @Nonnull BlockPos pos) {
		this.dimension = dim;
		
		this.position = pos.toImmutable();
	}
	
	@Nonnull
	public final DimensionType getDimension() {
		return this.dimension;
	}
	
	@Nonnull
	public final BlockPos getPosition() {
		return this.position;
	}
	
	@Override
	public final int hashCode() {
		return (this.dimension.hashCode() * 31 + this.position.hashCode()) ^ this.getClass().hashCode();
	}
	
	@Override
	public final boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null || o.getClass() != this.getClass())
			return false;
		
		AbstractBlockOrder o0 = (AbstractBlockOrder) o;
		
		return this.dimension == o0.dimension && this.position.equals(o0.position);
	}
}
