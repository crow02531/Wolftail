package net.wolftail.impl.core;

import net.wolftail.api.IClientEntryPoint;
import net.wolftail.api.IClientFrameCallback;
import net.wolftail.api.IServerEntryPoint;
import net.wolftail.api.UniversalPlayerType;

public final class ImplUPT implements UniversalPlayerType {
	
	private final IServerEntryPoint entrypoint_server;
	private final IClientEntryPoint entrypoint_client;
	
	private final IClientFrameCallback callback_client_frame;
	
	public ImplUPT() {
		this(null, null, null);
	}
	
	public ImplUPT(IServerEntryPoint arg0, IClientEntryPoint arg1, IClientFrameCallback arg2) {
		this.entrypoint_server = arg0;
		this.entrypoint_client = arg1;
		
		this.callback_client_frame = arg2;
	}
	
	public void callServerEnter(ImplPC.Server arg) {
		IServerEntryPoint ep = this.entrypoint_server;
		
		if(ep != null) ep.onEnter(arg);
	}
	
	public void callClientEnter(ImplPC.Client arg) {
		IClientEntryPoint ep = this.entrypoint_client;
		
		if(ep != null) ep.onEnter(arg);
	}
	
	public void callClientFrame(ImplPC.Client arg) {
		IClientFrameCallback cb = this.callback_client_frame;
		
		if(cb != null) cb.onFrame(arg);
	}
}
