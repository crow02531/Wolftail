package net.wolftail.util.tracker.builtin;

import javax.annotation.Nonnull;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.internal.tracker.ExtTrackerChunk;
import net.wolftail.internal.tracker.ExtTrackerWorldServer;
import net.wolftail.internal.util.MoreBlockPos;
import net.wolftail.util.tracker.DiffVisitor;
import net.wolftail.util.tracker.Timing;

public final class OrderTileEntity extends AbstractBlockOrder {
	
	static {
		addMechanism(() -> {
			for (WorldServer w : GameSection.serverInstance().worlds)
				((ExtTrackerWorldServer) w).wolftail_bte_assemble();
		});
	}
	
	/**
	 * Constructs a block tile entity order.
	 * 
	 * @param dim the dimension
	 * @param pos the block position, must within buildable area
	 */
	public OrderTileEntity(@Nonnull DimensionType dim, @Nonnull BlockPos pos) {
		super(dim, pos);
	}
	
	@Override
	public boolean track(@Nonnull MinecraftServer server, @Nonnull DiffVisitor acceptor, @Nonnull Timing timing) {
		return ((ExtTrackerChunk) server.getWorld(this.dimension.getId()).getChunkFromBlockCoords(this.position))
				.wolftail_bte_track(MoreBlockPos.toIndex(this.position), acceptor, timing);
	}
	
	@Override
	public boolean untrack(@Nonnull MinecraftServer server, @Nonnull DiffVisitor acceptor) {
		BlockPos pos = this.position;
		Chunk c = server.getWorld(this.dimension.getId()).getChunkProvider().getLoadedChunk(pos.getX() >> 4,
				pos.getZ() >> 4);
		
		return c == null ? false : ((ExtTrackerChunk) c).wolftail_bte_untrack(MoreBlockPos.toIndex(pos), acceptor);
	}
}
