package net.wolftail.impl.core;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.wolftail.api.INetHandler;
import net.wolftail.api.PlayContext;
import net.wolftail.impl.core.network.NptPacketListener;

public sealed abstract class ImplPC implements PlayContext permits ImplPCC, ImplPCS {
	
	final UUID identifier;
	final String name;
	
	final Connection connection;
	
	public ImplPC(UUID id, String name, Connection connect) {
		this.identifier = id;
		this.name = name;
		
		this.connection = connect;
	}
	
	public Connection getConnection() {
		return this.connection;
	}
	
	@Override
	public PacketFlow side() {
		return this.connection.getReceiving();
	}
	
	@Override
	public abstract ImplUPT playType();
	
	@Override
	public UUID playId() {
		return this.identifier;
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
	public void setNetHandler(INetHandler handler) {
		this.ensureNonPlayerType();
		
		((NptPacketListener) this.connection.getPacketListener()).setNetHandler(handler);
	}
	
	@Override
	public INetHandler getNetHandler() {
		this.ensureNonPlayerType();
		
		return ((NptPacketListener) this.connection.getPacketListener()).getNetHandler();
	}
	
	@Override
	public void disconnect() {
		this.connection.disconnect(null);
	}
	
	@Override
	public boolean isConnected() {
		return this.connection.isConnected();
	}
	
	protected final void ensureNonPlayerType() {
		if(this.playType().isPlayerType())
			throw new UnsupportedOperationException();
	}
}
