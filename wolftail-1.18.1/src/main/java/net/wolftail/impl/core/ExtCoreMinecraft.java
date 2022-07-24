package net.wolftail.impl.core;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;

public interface ExtCoreMinecraft {
	
	//called by logic client only
	ImplPCC wolftail_getContext();
	
	//called by netty thread, ask logic client to load play context and wait until it has done
	void wolftail_loadContext(ImplUPT type, UUID id, String name, Connection connect) throws InterruptedException, ExecutionException;
	
	//called by logic client only
	static boolean isNptPlaying() {
		ImplPCC c = ((ExtCoreMinecraft) Minecraft.getInstance()).wolftail_getContext();
		
		return c == null ? false : !c.playType().isPlayerType();
	}
}
