package net.wolftail.impl.core;

import net.wolftail.api.IEntryPoint;
import net.wolftail.api.UniversalPlayerType;

public final class ImplUPT implements UniversalPlayerType {
	
	private final IEntryPoint entrypoint_server;
	private final IEntryPoint entrypoint_client;
	
	public ImplUPT() {
		this(null, null);
	}
	
	public ImplUPT(IEntryPoint s, IEntryPoint c) {
		this.entrypoint_server = s;
		this.entrypoint_client = c;
	}
	
	public void callServerEnter(ImplPC arg) {
		if(this.entrypoint_server != null)
			this.entrypoint_server.onEnter(arg);
	}
	
	public void callClientEnter(ImplPC arg) {
		if(this.entrypoint_client != null)
			this.entrypoint_client.onEnter(arg);
	}
}
