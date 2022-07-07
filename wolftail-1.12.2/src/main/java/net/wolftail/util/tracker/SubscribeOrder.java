package net.wolftail.util.tracker;

import java.util.Objects;

import net.minecraft.world.DimensionType;

public final class SubscribeOrder {
	
	private DimensionType dim;
	
	private int chunkX;
	private int chunkY;
	
	public SubscribeOrder(DimensionType target, int chunkX, int chunkY) {
		this.dim = Objects.requireNonNull(target);
		
		this.chunkX = chunkX;
		this.chunkY = chunkY;
	}
	
	public DimensionType target() {
		return this.dim;
	}
	
	public int chunkX() {
		return this.chunkX;
	}
	
	public int chunkY() {
		return this.chunkY;
	}
}
