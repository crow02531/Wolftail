package net.wolftail.util.tracker;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;

/**
 * A kind of {@link ContentOrder}. Its type can only be
 * {@link ContentType#BLOCK_TILEENTITY BLOCK_TILEENTITY}. And the
 * params are {@link #dimension()}, {@link #blockX()}, {@link #blockY()}
 * , and {@link #blockZ()}.
 * 
 * @see ContentOrder
 */
@Immutable
@SideWith(section = GameSection.GAME_PLAYING)
public final class OrderBlockNormal extends ContentOrder {
	
	private final ContentType type;
	
	final DimensionType dim;
	final BlockPos pos;
	
	OrderBlockNormal(ContentType type, DimensionType target, BlockPos pos) {
		this.type = type;
		
		this.dim = target;
		this.pos = pos.toImmutable();
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
	
	@Nonnull
	public BlockPos position() {
		return this.pos;
	}
	
	@Override
	public int hashCode() {
		return this.dim.hashCode() * 31 + this.pos.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || !(o instanceof OrderBlockNormal)) return false;
		
		OrderBlockNormal o0 = (OrderBlockNormal) o;
		
		return this.dim == o0.dim && this.pos.equals(o0.pos);
	}
	
	@Nonnull
	@Override
	public String toString() {
		return this.type + ": " + this.dim
				+ "[" + this.pos.getX() + ", " + this.pos.getY() + ", "
				+ this.pos.getZ() + "]";
	}
}
