package net.wolftail.impl;

import net.wolftail.impl.SharedImpls.H3;
import net.wolftail.impl.SharedImpls.H6;

public interface ExtensionsChunk {
	
	boolean wolftail_hasSubscriber();
	
	void wolftail_register_CB(H3 subscribeEntry);
	boolean wolftail_unregister_CB(H6 wrapper);
	
	ExtensionsChunk wolftail_getNext();
	void wolftail_setNext(ExtensionsChunk c);
	
	void wolftail_postTick(int tick);
	
	void wolftail_blockChanged(int localX, int localY, int localZ);
}
