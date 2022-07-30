package net.wolftail.internal.tracker;

import net.wolftail.util.tracker.DiffVisitor;
import net.wolftail.util.tracker.Timing;

public interface ExtTrackerChunk {
	
	boolean wolftail_preventUnload();
	
	void wolftail_blockChanged(short index);
	
	void wolftail_tileEntityChanged(short index);
	
	boolean wolftail_cbs_track(DiffVisitor acceptor, Timing timing);
	
	boolean wolftail_cbs_untrack(DiffVisitor acceptor);
	
	boolean wolftail_bte_track(short index, DiffVisitor acceptor, Timing timing);
	
	boolean wolftail_bte_untrack(short index, DiffVisitor acceptor);
	
	void wolftail_cbs_assemble(int tick);
	
	void wolftail_bte_assemble(int tick);
}
