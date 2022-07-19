package net.wolftail.impl.tracker;

import java.util.IdentityHashMap;
import java.util.function.Consumer;

import net.wolftail.util.tracker.ContentDiff;
import net.wolftail.util.tracker.ContentTracker;

public interface ExtTrackerMinecraftServer {
	
	void wolftail_setTracker(ContentTracker ct);
	ContentTracker wolftail_getTracker();
	
	IdentityHashMap<Consumer<ContentDiff>, SubscriberWrapper> wolftail_wrappers();
	
	boolean wolftail_duringAssemble();
}
