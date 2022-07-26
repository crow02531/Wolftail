package net.wolftail.impl.core;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.util.text.ITextComponent;
import net.wolftail.impl.util.ByteBufs;

public final class ImplPCC extends ImplPC {
	
	final ImplUPT type;
	
	public ImplPCC(ImplUPT type, UUID id, String name, NetworkManager connect) {
		super(id, name, connect);
		
		this.type = type;
	}
	
	@Override
	public ImplUPT playType() {
		return this.type;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void sendPacket(ByteBuf buf, GenericFutureListener<? extends Future<? super Void>> listener) {
		this.ensureNonPlayerType();
		
		this.connection.sendPacket(new CPacketCustomPayload("WT|PL", ByteBufs.wrap(buf)), listener);
	}
	
	@Override
	public void disconnect(ITextComponent reason) {
		this.connection.closeChannel(reason);
	}
}
