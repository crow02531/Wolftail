package com.example.examplemod;

import io.netty.buffer.ByteBuf;
import net.wolftail.api.INetworkHandler;
import net.wolftail.api.IServerHandler;
import net.wolftail.api.PlayContext;

public final class PigServerHandler implements IServerHandler {

	static final PigServerHandler INSTANCE = new PigServerHandler();

	private PigServerHandler() {
	}

	@Override
	public void handleEnter(PlayContext context) {
		context.setHandler(new PigServerNetHandler());
	}

	@Override
	public void handleLeave(PlayContext context) {

	}

	private static class PigServerNetHandler implements INetworkHandler {

		PigServerNetHandler() {
		}

		@Override
		public void handle(ByteBuf buf) {
		}
	}
}
