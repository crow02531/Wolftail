package net.wolftail.impl;

import net.wolftail.util.tracker.ContentTracker;

public interface ExtensionsMinecraftServer {
	
	ImplMPCRoot wolftail_getRootManager();
	
	ContentTracker wolftail_getContentTracker();
	void wolftail_setContentTracker(ContentTracker obj);
}
