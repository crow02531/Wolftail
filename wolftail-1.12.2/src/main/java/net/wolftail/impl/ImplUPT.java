package net.wolftail.impl;

import net.wolftail.api.ClientEntryPoint;
import net.wolftail.api.ClientFrameCallback;
import net.wolftail.api.ServerEntryPoint;
import net.wolftail.api.UniversalPlayerType;

public final class ImplUPT implements UniversalPlayerType {
	
	private final ServerEntryPoint entrypoint_server;
	private final ClientEntryPoint entrypoint_client;
	
	private final ClientFrameCallback callback_client_frame;
	
	public ImplUPT() {
		this(null, null, null);
	}
	
	public ImplUPT(ServerEntryPoint arg0, ClientEntryPoint arg1, ClientFrameCallback arg2) {
		this.entrypoint_server = arg0;
		this.entrypoint_client = arg1;
		
		this.callback_client_frame = arg2;
	}
	
	public void callServerEnter(ImplPCServer arg) {
		ServerEntryPoint ep = this.entrypoint_server;
		
		if(ep != null) ep.onEnter(arg);
	}
	
	public void callClientEnter(ImplPCClient arg) {
		ClientEntryPoint ep = this.entrypoint_client;
		
		if(ep != null) ep.onEnter(arg);
	}
	
	public void callClientFrame(ImplPCClient arg) {
		ClientFrameCallback cb = this.callback_client_frame;
		
		if(cb != null) cb.onFrame(arg);
	}
}
