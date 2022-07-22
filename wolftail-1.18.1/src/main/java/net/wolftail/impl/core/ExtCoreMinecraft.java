package net.wolftail.impl.core;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import net.minecraft.network.Connection;

public interface ExtCoreMinecraft {
	
	//called by netty thread
	void wolftail_loadContext(ImplUPT type, UUID id, String name, Connection connect) throws InterruptedException, ExecutionException;
}
