package net.wolftail.impl;

import java.util.function.Consumer;

import net.wolftail.impl.SharedImpls.H3;
import net.wolftail.util.tracker.ContentDiff;

public interface ExtensionsWorldServer {
	
	ExtensionsChunk wolftail_getHead();
	void wolftail_setHead(ExtensionsChunk h);
	
	void wolftail_register_WW(H3 subscribeEntry);
	void wolftail_unregister_WW(Consumer<ContentDiff> subscriber);
	
	void wolftail_register_WDT(H3 subscribeEntry);
	void wolftail_unregister_WDT(Consumer<ContentDiff> subscriber);
	
	void wolftail_postTick(int tick);
}
