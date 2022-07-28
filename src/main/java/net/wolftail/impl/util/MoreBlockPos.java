package net.wolftail.impl.util;

import net.minecraft.util.math.BlockPos;

public final class MoreBlockPos {
	
	private MoreBlockPos() {}
	
	public static short toIndex(int localX, int localY, int localZ) {
		return (short) (localX << 12 | localZ << 8 | localY);
	}
	
	public static short toIndex(BlockPos worldPos) {
		return toIndex(worldPos.getX() & 0xF, worldPos.getY(), worldPos.getZ() & 0xF);
	}
}
