package com.example.examplemod;

import io.netty.buffer.ByteBuf;
import net.wolftail.api.IClientHandler;
import net.wolftail.api.INetworkHandler;
import net.wolftail.api.PlayContext;

public final class PigClientHandler implements IClientHandler, INetworkHandler {
	
	static final PigClientHandler INSTANCE = new PigClientHandler();
	
	private PigClientHandler() {
	}
	
	@Override
	public void handleEnter(PlayContext context) {
		context.setHandler(this);
	}
	
	@Override
	public void handleFrame() {
		
	}
	
	@Override
	public void handleLeave() {
		
	}
	
	@Override
	public void handle(ByteBuf buf) {
		
	}
}
