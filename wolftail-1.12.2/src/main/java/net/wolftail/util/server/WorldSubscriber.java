package net.wolftail.util.server;

import net.minecraft.world.WorldServer;

public interface WorldSubscriber {
	
	void subscribeGlobal(IWorldListener listener);
	
	void unsubscribeGlobal(IWorldListener listener);
	
	void subscribeChunk(IWorldListener listener, int chunkX, int chunkY);
	
	void unsubscribeChunk(IWorldListener listener, int chunkX, int chunkY);
	
	public static WorldSubscriber instanceFor(WorldServer world) {
		return null;
	}
}
