package net.wolftail.internal.core;

import java.util.UUID;

import io.netty.buffer.ByteBufAllocator;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.wolftail.api.INetworkHandler;
import net.wolftail.api.PlayContext;
import net.wolftail.internal.core.network.NptPacketListener;

public abstract class ImplPC implements PlayContext {
	
	final UUID identifier;
	final String name;
	
	final NetworkManager connection;
	
	ImplPC(UUID id, String name, NetworkManager connect) {
		this.identifier = id;
		this.name = name;
		
		this.connection = connect;
	}
	
	public NetworkManager getConnection() {
		return this.connection;
	}
	
	@Override
	public EnumPacketDirection side() {
		return this.connection.getDirection();
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
	public ByteBufAllocator alloc() {
		return this.connection.channel().alloc();
	}
	
	@Override
	public void setHandler(INetworkHandler handler) {
		this.ensureNonPlayerType();
		
		((NptPacketListener) this.connection.getNetHandler()).setNetHandler(handler);
	}
	
	@Override
	public INetworkHandler getHandler() {
		this.ensureNonPlayerType();
		
		return ((NptPacketListener) this.connection.getNetHandler()).getNetHandler();
	}
	
	@Override
	public boolean isConnected() {
		return this.connection.isChannelOpen();
	}
	
	final void ensureNonPlayerType() {
		if (this.playType().isPlayerType())
			throw new UnsupportedOperationException();
	}
}
