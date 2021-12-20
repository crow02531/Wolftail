package net.wolftail.impl;

import java.util.UUID;

import net.minecraft.network.NetworkManager;

public interface ExtensionsMinecraft {
	
	ImplPCClient wolftail_setupPlayContext(ImplUPT type, UUID id, NetworkManager connect);
	
	void wolftail_unloadPlayContext();
}
