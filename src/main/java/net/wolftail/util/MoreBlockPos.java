package net.wolftail.util;

import javax.annotation.Nonnull;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;

public final class MoreBlockPos {
	
	private MoreBlockPos() {
	}
	
	/**
	 * Calculate the index of a chunk-based coordinate
	 * {@code (localX, localY, localZ)}.
	 * 
	 * @param localX {@code 0 <= localX < 16}
	 * @param localY {@code 0 <= localY < 256}
	 * @param localZ {@code 0 <= localZ < 16}
	 * 
	 * @return the index, 16-bits
	 */
	public static short toIndex(int localX, int localY, int localZ) {
		return (short) (localX << 12 | localZ << 8 | localY);
	}
	
	/**
	 * @param worldPos a block pos with its {@code y} inside {@code [0, 256)}.
	 * 
	 * @return the index, 16-bits
	 */
	public static short toIndex(@Nonnull BlockPos worldPos) {
		return toIndex(worldPos.getX() & 0xF, worldPos.getY(), worldPos.getZ() & 0xF);
	}
	
	@Nonnull
	public static BlockPos toPos(int chunkX, int chunkZ, short index) {
		return new BlockPos((chunkX << 4) + (index >> 12 & 0xF), index & 0xFF, (chunkZ << 4) + (index >> 8 & 0xF));
	}
	
	@Nonnull
	public static <T extends MutableBlockPos> T toPos(int chunkX, int chunkZ, short index, @Nonnull T dst) {
		dst.setPos((chunkX << 4) + (index >> 12 & 0xF), index & 0xFF, (chunkZ << 4) + (index >> 8 & 0xF));
		
		return dst;
	}
}
