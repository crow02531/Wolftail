package net.wolftail.util.tracker.builtin;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.DimensionType;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.impl.tracker.ExtTrackerWorldServer;
import net.wolftail.util.tracker.DiffVisitor;
import net.wolftail.util.tracker.Timing;

@Immutable
@SideWith(section = GameSection.GAME_PLAYING)
public final class OrderDaytime extends AbstractWorldOrder {
	
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
