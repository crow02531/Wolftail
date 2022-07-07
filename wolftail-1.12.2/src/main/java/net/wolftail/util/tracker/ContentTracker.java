package net.wolftail.util.tracker;

import java.util.function.Consumer;

import net.minecraft.server.MinecraftServer;
import net.wolftail.impl.ExtensionsMinecraftServer;
import net.wolftail.impl.SharedImpls;

public final class ContentTracker {
	
	private ContentTracker() {
		
	}
	
	public void subscribe(SubscribeOrder order, Consumer<ContentDiff> subscriber) {
		
	}
	
	public void unsubscribe(SubscribeOrder order, Consumer<ContentDiff> subscriber) {
		
	}
	
	public static ContentTracker instanceFor(MinecraftServer server) {
		ExtensionsMinecraftServer ext = SharedImpls.as(server);
		ContentTracker r = ext.wolftail_getContentTracker();
		
		if(r == null) {
			synchronized(LOCK) {
				r = ext.wolftail_getContentTracker();
				
				if(r == null)
					ext.wolftail_setContentTracker(r = new ContentTracker());
			}
		}
		
		return r;
	}
	
	private static final Object LOCK = new Object();
}
