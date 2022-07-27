package net.wolftail.impl.tracker;

public interface ExtTrackerChunk {
	
	boolean wolftail_preventUnload();
	
	void wolftail_assemble(int tick);
}
