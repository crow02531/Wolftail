package net.wolftail.impl;

import java.util.UUID;

import net.minecraft.network.NetworkManager;

public interface ExtensionsMinecraft {
	
	void wolftail_loginSuccess(ImplUPT type, UUID id, NetworkManager connect); //called by netty thread
}
