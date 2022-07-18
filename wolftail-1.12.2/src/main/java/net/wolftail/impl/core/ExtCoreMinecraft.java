package net.wolftail.impl.core;

import java.util.UUID;

import net.minecraft.network.NetworkManager;

public interface ExtCoreMinecraft {
	
	void wolftail_loginSuccess(ImplUPT type, UUID id, NetworkManager connect); //called by netty thread
}
