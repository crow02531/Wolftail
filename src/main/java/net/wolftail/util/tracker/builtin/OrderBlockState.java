package net.wolftail.util.tracker.builtin;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.DimensionType;
import net.minecraft.world.chunk.Chunk;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.impl.tracker.ExtTrackerChunk;
import net.wolftail.util.tracker.DiffVisitor;
import net.wolftail.util.tracker.Timing;

@Immutable
@SideWith(section = GameSection.GAME_PLAYING)
public final class OrderBlockState extends AbstractChunkOrder {
	
	/**
	 * Constructs a chunk block state order.
	 * 
	 * @param dim	the dimension
	 * @param x		chunkX, {@code -1875000 <= x < 1875000}
	 * @param z		chunkZ, {@code -1875000 <= z < 1875000}
	 */
	public OrderBlockState(@Nonnull DimensionType dim, int x, int z) {
		super(dim, x, z);
	}
	
	@Override
	public boolean track(@Nonnull MinecraftServer server, @Nonnull DiffVisitor acceptor, @Nonnull Timing timing) {
		return ((ExtTrackerChunk) server.getWorld(this.dimension.getId())
				.getChunkFromChunkCoords(this.chunkX, this.chunkZ))
				.wolftail_cbs_track(acceptor, timing);
	}
	
	@Override
	public boolean untrack(@Nonnull MinecraftServer server, @Nonnull DiffVisitor acceptor) {
		Chunk c = server.getWorld(this.dimension.getId()).getChunkProvider().getLoadedChunk(this.chunkX, this.chunkZ);
		
		return c == null ? false : ((ExtTrackerChunk) c).wolftail_cbs_untrack(acceptor);
	}
}