package net.wolftail.impl;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.server.SPacketLoginSuccess;

public interface ExtensionsNetHandlerLoginClient {
	
	SPacketLoginSuccess wolftail_getStoredLoginSuccessPacket();
	
	NetworkManager wolftail_getConnection();
	
	void wolftail_npt_clearStoredPacketRef();
}
