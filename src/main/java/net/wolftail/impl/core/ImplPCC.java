package net.wolftail.impl.core;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.wolftail.impl.core.network.Constants;

public final class ImplPCC extends ImplPC {
	
	final ImplUPT type;
	
	public ImplPCC(ImplUPT type, UUID id, String name, Connection connect) {
		super(id, name, connect);
		
		this.type = type;
	}

	@Override
	public ImplUPT playType() {
		return this.type;
	}
	
	@Override
	public void sendPacket(ByteBuf buf, GenericFutureListener<? extends Future<? super Void>> listener) {
		this.ensureNonPlayerType();
		
		this.connection.send(new ServerboundCustomPayloadPacket(Constants.CHANNEL_PLAY_PAYLOAD, newOrReturn(buf)), listener);
	}
	
	@Override
	public void disconnect(Component reason) {
		this.connection.disconnect(reason);
	}
}
