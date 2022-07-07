package net.wolftail.impl;

import java.util.function.Consumer;

import net.wolftail.util.tracker.ContentDiff;

public interface ExtensionsChunk {
	
	boolean wolftail_hasSubscriber();
	
	void wolftail_register(Consumer<ContentDiff> subscriber);
	void wolftail_unregister(Consumer<ContentDiff> subscriber);
	
	ExtensionsChunk wolftail_getNext();
	void wolftail_setNext(ExtensionsChunk c);
	
	void wolftail_tick();
}
