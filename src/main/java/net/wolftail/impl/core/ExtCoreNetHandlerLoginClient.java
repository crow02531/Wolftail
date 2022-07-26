package net.wolftail.impl.core;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.server.SPacketLoginSuccess;

public interface ExtCoreNetHandlerLoginClient {
	
	SPacketLoginSuccess wolftail_getStoredLoginSuccessPacket();
	void wolftail_clearStoredPacketRef();
	
	NetworkManager wolftail_getConnection();
}
