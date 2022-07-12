package net.wolftail.impl;

import java.util.function.Consumer;

import net.wolftail.util.tracker.ContentDiff;

public interface ExtensionsWorldServer {
	
	ExtensionsChunk wolftail_getHead();
	void wolftail_setHead(ExtensionsChunk h);
	
	void wolftail_register(Consumer<ContentDiff> subscriber);
	void wolftail_unregister(Consumer<ContentDiff> subscriber);
}
