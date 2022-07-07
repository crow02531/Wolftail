package net.wolftail.util.tracker;

import java.util.Objects;

import javax.annotation.Nonnull;

import net.minecraft.world.DimensionType;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;

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
}
