package net.wolftail.impl.core;

import java.util.UUID;

import net.minecraft.network.NetworkManager;

public interface ExtCoreMinecraft {
	
	// called by netty thread, ask logic client to load play context and wait until
	// it has done
	void wolftail_loadContext(ImplUPT type, UUID id, String name, NetworkManager connect);
}
