package net.wolftail.impl.tracker;

import net.minecraft.world.chunk.Chunk;
import net.wolftail.impl.util.collect.LinkedObjectCollection;
import net.wolftail.util.tracker.DiffVisitor;
import net.wolftail.util.tracker.Timing;

public interface ExtTrackerWorldServer {
	
	boolean wolftail_wdt_track(DiffVisitor acceptor, Timing timing);
	
	boolean wolftail_wdt_untrack(DiffVisitor acceptor);
	
	void wolftail_wdt_assemble();
	
	void wolftail_cbs_assemble();
	
	LinkedObjectCollection<Chunk>.Node wolftail_join(Chunk c);
}
