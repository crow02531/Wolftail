package com.example.examplemod.server;

import net.wolftail.api.IServerEntryPoint;
import net.wolftail.api.ServerPlayContext;

public class ServerCallback implements IServerEntryPoint {
	
	@Override
	public void onEnter(ServerPlayContext context) {
		context.setNetHandler(new ServerNetHandler(context));
	}
}
