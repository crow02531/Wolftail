package net.wolftail.impl;

import net.wolftail.impl.SharedImpls.H3;
import net.wolftail.impl.SharedImpls.H6;

public interface ExtensionsChunk {
	
	boolean wolftail_hasSubscriber();
	
	void wolftail_register_CB(H3 subscribeEntry);
	boolean wolftail_unregister_CB(H6 wrapper);
	
	void wolftail_register_BTE(H3 subscribeEntry, short index);
	boolean wolftail_unregister_BTE(H6 wrapper, short index);
	
	void wolftail_blockChanged(short index);
	void wolftail_tileEntityChanged(short index);
	
	void wolftail_postTick(int tick);
}
