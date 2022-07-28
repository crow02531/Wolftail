package net.wolftail.impl.tracker;

import net.wolftail.util.tracker.DiffVisitor;
import net.wolftail.util.tracker.Timing;

public interface ExtTrackerChunk {
	
	boolean wolftail_preventUnload();
	
	void wolftail_blockChanged(short index);
	void wolftail_tileEntityChanged(short index);
	
	boolean wolftail_cbs_track(DiffVisitor acceptor, Timing timing);
	boolean wolftail_cbs_untrack(DiffVisitor acceptor);
	
	void wolftail_assemble(int tick);
}
