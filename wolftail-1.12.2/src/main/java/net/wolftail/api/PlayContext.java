package net.wolftail.api;

import java.util.UUID;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

public interface PlayContext {
	
	UniversalPlayerType playType();
	
	UUID playId();
	
	String playName();
	
	default ClientPlayContext asClient() {
		return (ClientPlayContext) this;
	}
	
	default ServerPlayContext asServer() {
		return (ServerPlayContext) this;
	}
	
	void sendPacket(Packet<?> packetIn);
	
	void sendPacket(Packet<?> packetIn, GenericFutureListener<? extends Future<? super Void>> listener);
	
	void disconnect();
	
	void setNetHandler(INetHandler handler);
	
	boolean abandoned();
}
