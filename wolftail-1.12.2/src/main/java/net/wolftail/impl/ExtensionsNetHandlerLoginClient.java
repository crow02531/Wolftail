package net.wolftail.impl;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.server.SPacketLoginSuccess;

public interface ExtensionsNetHandlerLoginClient {
	
	SPacketLoginSuccess wolftail_getStoredLoginSuccessPacket();
	void wolftail_clearStoredPacketRef();
	
	NetworkManager wolftail_getConnection();
}
