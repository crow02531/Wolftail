package net.wolftail.impl.core;

import net.wolftail.api.IClientHandler;
import net.wolftail.api.IServerHandler;
import net.wolftail.api.UniversalPlayerType;

public final class ImplUPT implements UniversalPlayerType {
	
	private final IServerHandler handler_server;
	private final IClientHandler handler_client;
	
	public ImplUPT() {
		this(null, null);
	}
	
	public ImplUPT(IServerHandler s, IClientHandler c) {
		this.handler_server = s;
		this.handler_client = c;
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
	
	public void callClientLeave() {
		if (this.handler_client != null)
			this.handler_client.handleLeave();
	}
}
