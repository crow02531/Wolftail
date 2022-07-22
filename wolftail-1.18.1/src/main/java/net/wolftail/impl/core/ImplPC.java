package net.wolftail.impl.core;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.wolftail.api.INetHandler;
import net.wolftail.api.PlayContext;

public sealed abstract class ImplPC implements PlayContext permits ImplPCC, ImplPCS {
	
	final UUID identifier;
	final String name;
	
	final Connection connection;
	
	public ImplPC(UUID id, String name, Connection connect) {
		this.identifier = id;
		this.name = name;
		
		this.connection = connect;
	}
	
	@Override
	public PacketFlow side() {
		return this.connection.getReceiving();
	}
	
	@Override
	public abstract ImplUPT playType();
	
	@Override
	public UUID playId() {
		return null;
	}

	@Override
	public String playName() {
		return this.name;
	}
	
	@Override
	public void sendPacket(ByteBuf buf) {
		this.sendPacket(buf, null);
	}
	
	@Override
	public void sendPacket(ByteBuf buf, GenericFutureListener<? extends Future<? super Void>> listener) {
		if(this.playType().isPlayerType())
			throw new UnsupportedOperationException();
		
		
	}
	
	@Override
	public void setNetHandler(INetHandler handler) {
		if(this.playType().isPlayerType())
			throw new UnsupportedOperationException();
		
		
	}
	
	@Override
	public void disconnect() {
		this.connection.disconnect(null);
	}
	
	@Override
	public boolean isConnected() {
		return this.connection.isConnected();
	}
}
