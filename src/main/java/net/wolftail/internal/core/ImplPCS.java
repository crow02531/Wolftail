package net.wolftail.internal.core;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.wolftail.util.MoreByteBufs;

public final class ImplPCS extends ImplPC {
	
	final ImplMPCS subManager;
	
	public ImplPCS(ImplMPCS subManager, UUID id, String name, NetworkManager connect) {
		super(id, name, connect);
		
		this.subManager = subManager;
	}
	
	public ImplMPCS subManager() {
		return this.subManager;
	}
	
	@Override
	public ImplUPT playType() {
		return this.subManager.type;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void send(ByteBuf buf, GenericFutureListener<? extends Future<? super Void>> listener) {
		this.ensureNonPlayerType();
		
		this.connection.sendPacket(new SPacketCustomPayload("WT|PL", MoreByteBufs.wrap(buf)), f -> buf.release(), listener);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void disconnect(ITextComponent reason) {
		final ITextComponent r = reason == null ? new TextComponentTranslation("multiplayer.disconnect.generic")
				: reason;
		
		this.connection.sendPacket(new SPacketDisconnect(r), f -> this.connection.closeChannel(r));
		this.connection.disableAutoRead();
	}
}
