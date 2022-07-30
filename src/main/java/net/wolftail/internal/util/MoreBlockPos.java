package net.wolftail.internal.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;

public final class MoreBlockPos {
	
	private MoreBlockPos() {
	}
	
	public static short toIndex(int localX, int localY, int localZ) {
		return (short) (localX << 12 | localZ << 8 | localY);
	}
	
	public static short toIndex(BlockPos worldPos) {
		return toIndex(worldPos.getX() & 0xF, worldPos.getY(), worldPos.getZ() & 0xF);
	}
	
	public static BlockPos toPos(int chunkX, int chunkZ, short index) {
		return new BlockPos(chunkX << 4 + index >> 12 & 0xF, index & 0xFF, chunkZ << 4 + index >> 8 & 0xF);
	}
	
	public static MutableBlockPos toPos(int chunkX, int chunkZ, short index, MutableBlockPos dst) {
		dst.setPos(chunkX << 4 + index >> 12 & 0xF, index & 0xFF, chunkZ << 4 + index >> 8 & 0xF);
		
		return dst;
	}
}
