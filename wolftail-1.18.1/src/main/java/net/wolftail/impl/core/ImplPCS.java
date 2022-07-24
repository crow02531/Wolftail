package net.wolftail.impl.core;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.wolftail.impl.core.network.Constants;

public final class ImplPCS extends ImplPC {
	
	final ImplMPCS subManager;
	
	public ImplPCS(ImplMPCS subManager, UUID id, String name, Connection connect) {
		super(id, name, connect);
		
		this.subManager = subManager;
	}
	
	@Override
	public ImplUPT playType() {
		return this.subManager.type;
	}
	
	@Override
	public void sendPacket(ByteBuf buf, GenericFutureListener<? extends Future<? super Void>> listener) {
		this.ensureNonPlayerType();
		
		this.connection.send(new ClientboundCustomPayloadPacket(Constants.CHANNEL_PLAY_PAYLOAD, Constants.newOrReturn(buf)), listener);
	}
	
	@Override
	public void disconnect(Component reason) {
		this.connection.send(new ClientboundDisconnectPacket(reason), f -> this.connection.disconnect(reason));
		this.connection.setReadOnly();
	}
	
	public ImplMPCS subManager() {
		return this.subManager;
	}
}
