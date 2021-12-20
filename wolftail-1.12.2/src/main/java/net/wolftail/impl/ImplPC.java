package net.wolftail.impl;

import java.util.UUID;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.wolftail.api.PlayContext;

public abstract class ImplPC implements PlayContext {
	
	final UUID identifier;
	final String name;
	
	final NetworkManager connection;
	
	public volatile Packet<?> keepAlive_receivedPkt;
	public long keepAlive_timer;
	
	ImplPC(UUID arg0, String arg1, NetworkManager arg2) {
		this.identifier = arg0;
		this.name = arg1;
		
		this.connection = arg2;
	}
	
	@Override
	public UUID playId() {
		return this.identifier;
	}
	
	@Override
	public String playName() {
		return this.name;
	}
	
	@Override
	public void sendPacket(Packet<?> packetIn) {
		this.connection.sendPacket(packetIn);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void sendPacket(Packet<?> packetIn, GenericFutureListener<? extends Future<? super Void>> listener) {
		this.connection.sendPacket(packetIn, listener);
	}
	
	@Override
	public void disconnect() {
		this.connection.closeChannel(null);
	}
	
	@Override
	public void setNetHandler(INetHandler handler) {
		this.connection.setNetHandler(handler);
	}
}
