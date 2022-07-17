package net.wolftail.impl;

import java.util.IdentityHashMap;
import java.util.Random;
import java.util.function.Consumer;

import net.wolftail.impl.SharedImpls.H6;
import net.wolftail.util.tracker.ContentDiff;
import net.wolftail.util.tracker.ContentTracker;

public interface ExtensionsMinecraftServer {
	
	ImplMPCR wolftail_getRootManager();
	
	ContentTracker wolftail_getContentTracker();
	void wolftail_setContentTracker(ContentTracker obj);
	
	boolean wolftail_duringSending();
	
	IdentityHashMap<Consumer<ContentDiff>, H6> wolftail_wrappers();
	
	Random wolftail_random();
}
