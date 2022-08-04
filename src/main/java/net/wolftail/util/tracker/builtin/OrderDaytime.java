package net.wolftail.util.tracker.builtin;

import javax.annotation.Nonnull;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.wolftail.internal.tracker.ExtTrackerWorldServer;
import net.wolftail.util.MoreServers;
import net.wolftail.util.tracker.DiffVisitor;
import net.wolftail.util.tracker.Timing;

public final class OrderDaytime extends AbstractWorldOrder {
	
	static {
		addMechanism(() -> {
			for (WorldServer w : MoreServers.serverInstance().worlds)
				((ExtTrackerWorldServer) w).wolftail_wdt_assemble();
		});
	}
	
	public OrderDaytime(@Nonnull DimensionType dim) {
		super(dim);
	}
	
	@Override
	public boolean track(@Nonnull MinecraftServer server, @Nonnull DiffVisitor acceptor, @Nonnull Timing timing) {
		return ((ExtTrackerWorldServer) server.getWorld(this.dimension.getId())).wolftail_wdt_track(acceptor, timing);
	}
	
	@Override
	public boolean untrack(@Nonnull MinecraftServer server, @Nonnull DiffVisitor acceptor) {
		return ((ExtTrackerWorldServer) server.getWorld(this.dimension.getId())).wolftail_wdt_untrack(acceptor);
	}
}
