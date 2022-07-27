package net.wolftail.impl.tracker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.wolftail.impl.tracker.ExtTrackerChunk;
import net.wolftail.impl.tracker.ExtTrackerWorldServer;
import net.wolftail.impl.tracker.TrackContainer;
import net.wolftail.impl.util.collect.LinkedObjectCollection;
import net.wolftail.util.tracker.DiffVisitor;
import net.wolftail.util.tracker.Timing;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer implements ExtTrackerWorldServer {
	
	@Unique
	private final TrackContainer<Void> wdt = new TrackContainer<>(3);
	
	@Unique
	private final LinkedObjectCollection<Chunk> chunks = new LinkedObjectCollection<>();
	
	@Override
	public boolean wolftail_wdt_track(DiffVisitor acceptor, Timing timing) {
		return this.wdt.add(timing, acceptor);
	}
	
	@Override
	public boolean wolftail_wdt_untrack(DiffVisitor acceptor) {
		return this.wdt.remove(acceptor);
	}
	
	@Override
	public void wolftail_assemble(int tick) {
		this.wdt.forEach(tick, r -> r.assemble((dv, b) -> {
			WorldProvider wp = ((WorldServer) (Object) this).provider;
			
			dv.jzBegin();
			dv.jzBindWorld(wp.getDimensionType());
			dv.jzSetDaytime(wp.getWorldTime());
			dv.jzEnd();
		}));
		
		this.chunks.forEach(c -> ((ExtTrackerChunk) c).wolftail_assemble(tick));
	}
	
	@Override
	public LinkedObjectCollection<Chunk>.Node wolftail_join(Chunk c) {
		return this.chunks.enter(c);
	}
}
