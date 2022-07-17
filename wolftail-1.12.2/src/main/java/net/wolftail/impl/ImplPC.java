package net.wolftail.impl;

import java.util.UUID;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.wolftail.api.ClientPlayContext;
import net.wolftail.api.PlayContext;
import net.wolftail.api.ServerPlayContext;
import net.wolftail.api.SubPlayContextManager;
import net.wolftail.impl.network.BiDPacketNPTKeepAlive;

public abstract class ImplPC implements PlayContext {
	
	final UUID identifier;
	final String name;
	
	final NetworkManager connection;
	
	public volatile Packet<?> keepAlive_receivedPkt;
	public long keepAlive_timer;
	
	private ImplPC(UUID arg0, String arg1, NetworkManager arg2) {
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
	
	public static final class Client extends ImplPC implements ClientPlayContext {
		
		final ImplUPT type;
		
		public Client(ImplUPT arg0, UUID arg1, String arg2, NetworkManager arg3) {
			super(arg1, arg2, arg3);
			
			this.type = arg0;
		}
		
		@Override
		public ImplUPT playType() {
			return this.type;
		}
		
		public NetworkManager getConnection() {
			return this.connection;
		}
	}
	
	public static final class Server extends ImplPC implements ServerPlayContext {

		final ImplMPCS manager;
		
		public Server(ImplMPCS arg0, UUID arg1, String arg2, NetworkManager arg3) {
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
}
