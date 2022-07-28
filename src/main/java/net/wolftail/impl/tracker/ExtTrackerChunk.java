package net.wolftail.impl.tracker;

public interface ExtTrackerChunk {
	
	boolean wolftail_preventUnload();
	
	void wolftail_blockChanged(short index);
	void wolftail_tileEntityChanged(short index);
	
	void wolftail_assemble(int tick);
}
