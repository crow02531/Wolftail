package net.wolftail.impl;

import java.util.UUID;

import net.minecraft.network.NetworkManager;
import net.wolftail.api.ServerPlayContext;
import net.wolftail.api.SubPlayContextManager;
import net.wolftail.impl.network.BiDPacketNPTKeepAlive;

public final class ImplPCServer extends ImplPC implements ServerPlayContext {
	
	final ImplMPCSub manager;
	
	public ImplPCServer(ImplMPCSub arg0, UUID arg1, String arg2, NetworkManager arg3) {
		super(arg1, arg2, arg3);
		
		this.keepAlive_receivedPkt = new BiDPacketNPTKeepAlive();
		
		this.manager = arg0;
	}
	
	@Override
	public ImplUPT playType() {
		return this.manager.type;
	}
	
	@Override
	public SubPlayContextManager manager() {
		return this.manager;
	}
}
