package net.wolftail.impl;

import java.util.function.Consumer;

import net.wolftail.impl.SharedImpls.H3;
import net.wolftail.util.tracker.ContentDiff;

public interface ExtensionsChunk {
	
	boolean wolftail_hasSubscriber();
	
	void wolftail_register_CB(H3 subscribeEntry);
	void wolftail_unregister_CB(Consumer<ContentDiff> subscriber);
	
	ExtensionsChunk wolftail_getNext();
	void wolftail_setNext(ExtensionsChunk c);
	
	void wolftail_postTick(int tick);
	
	void wolftail_blockChanged(int localX, int localY, int localZ);
}
