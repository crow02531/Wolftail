package net.wolftail.internal.core;

import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import net.wolftail.api.IClientHandler;
import net.wolftail.api.IServerHandler;
import net.wolftail.api.Introduction;
import net.wolftail.api.UniversalPlayerType;

public final class ImplUPT extends Impl<UniversalPlayerType> implements UniversalPlayerType {
	
	public final Introduction introduction;
	
	private final IServerHandler handler_server;
	private final IClientHandler handler_client;
	
	public ImplUPT(IServerHandler s, IClientHandler c, Introduction i) {
		this.handler_server = s;
		this.handler_client = c;
		
		this.introduction = i;
	}
	
	public void callServerEnter(ImplPC arg) {
		if (this.handler_server != null)
			this.handler_server.handleEnter(arg);
	}
	
	public void callServerLeave(ImplPC arg) {
		if (this.handler_server != null)
			this.handler_server.handleLeave(arg);
	}
	
	public void callClientEnter(ImplPC arg) {
		if (this.handler_client != null)
			this.handler_client.handleEnter(arg);
	}
	
	public void callClientFrame() {
		if (this.handler_client != null)
			this.handler_client.handleFrame();
	}
	
	public void callClientChat(ChatType type, ITextComponent text) {
		if (this.handler_client != null)
			this.handler_client.handleChat(type, text);
	}
	
	public void callClientLeave() {
		if (this.handler_client != null)
			this.handler_client.handleLeave();
	}
	
	public boolean isSteve() {
		return this == TYPE_PLAYER;
	}
	
	@Override
	public boolean hasRegistered() {
		return this.delegate.name() != null;
	}
}
