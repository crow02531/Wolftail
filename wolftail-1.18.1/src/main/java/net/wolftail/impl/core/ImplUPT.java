package net.wolftail.impl.core;

import net.wolftail.api.IClientListener;
import net.wolftail.api.IServerListener;
import net.wolftail.api.UniversalPlayerType;

public final class ImplUPT implements UniversalPlayerType {
	
	private final IServerListener listener_server;
	private final IClientListener listener_client;
	
	public ImplUPT() {
		this(null, null);
	}
	
	public ImplUPT(IServerListener s, IClientListener c) {
		this.listener_server = s;
		this.listener_client = c;
	}
	
	public void callServerEnter(ImplPC arg) {
		if(this.listener_server != null)
			this.listener_server.onEnter(arg);
	}
	
	public void callServerLeave(ImplPC arg) {
		if(this.listener_server != null)
			this.listener_server.onLeave(arg);
	}
	
	public void callClientEnter(ImplPC arg) {
		if(this.listener_client != null)
			this.listener_client.onEnter(arg);
	}
	
	public void callClientRender() {
		if(this.listener_client != null)
			this.listener_client.onRender();
	}
	
	public void callClientLeave() {
		if(this.listener_client != null)
			this.listener_client.onLeave();
	}
}
