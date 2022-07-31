package net.wolftail.internal.tracker;

import net.minecraft.world.chunk.Chunk;
import net.wolftail.internal.util.collect.LinkedObjectCollection;
import net.wolftail.util.tracker.DiffVisitor;
import net.wolftail.util.tracker.Timing;

public interface ExtTrackerWorldServer {
	
	boolean wolftail_wdt_track(DiffVisitor acceptor, Timing timing);
	
	boolean wolftail_wdt_untrack(DiffVisitor acceptor);
	
	boolean wolftail_ww_track(DiffVisitor acceptor, Timing timing);
	
	boolean wolftail_ww_untrack(DiffVisitor acceptor);
	
	void wolftail_wdt_assemble();
	
	void wolftail_ww_assemble();
	
	void wolftail_cbs_assemble();
	
	void wolftail_bte_assemble();
	
	LinkedObjectCollection<Chunk>.Node wolftail_join(Chunk c);
}
