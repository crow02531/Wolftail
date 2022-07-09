package net.wolftail.impl;

import java.util.function.Consumer;

import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.wolftail.util.tracker.ContentDiff;

public interface ExtensionsChunk {
	
	boolean wolftail_hasSubscriber();
	
	void wolftail_register(Consumer<ContentDiff> subscriber);
	void wolftail_unregister(Consumer<ContentDiff> subscriber);
	
	ExtensionsChunk wolftail_getNext();
	void wolftail_setNext(ExtensionsChunk c);
	
	void wolftail_tick();
	
	ShortSet wolftail_changedBlocks();
	void wolftail_blockChanged(int x, int y, int z);
}
