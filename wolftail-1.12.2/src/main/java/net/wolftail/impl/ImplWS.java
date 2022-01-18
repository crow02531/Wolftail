package net.wolftail.impl;

import net.wolftail.util.server.IWorldListener;
import net.wolftail.util.server.WorldSubscriber;

public final class ImplWS implements WorldSubscriber {
	
	@Override
	public void subscribeGlobal(IWorldListener listener) {
		
	}
	
	@Override
	public void unsubscribeGlobal(IWorldListener listener) {
		
	}
	
	@Override
	public void subscribeChunk(IWorldListener listener, int chunkX, int chunkY) {
		
	}
	
	@Override
	public void unsubscribeChunk(IWorldListener listener, int chunkX, int chunkY) {
		
	}
}
