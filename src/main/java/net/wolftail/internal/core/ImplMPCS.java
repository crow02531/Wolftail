package net.wolftail.internal.core;

import java.util.UUID;

import net.wolftail.api.SubPlayContextManager;

public final class ImplMPCS implements SubPlayContextManager {
	
	final ImplMPCR root;
	final ImplUPT type;
	
	int current_load;
	
	ImplMPCS(ImplUPT arg0, ImplMPCR arg1) {
		this.type = arg0;
		this.root = arg1;
	}
	
	@Override
	public ImplMPCR rootManager() {
		return this.root;
	}
	
	@Override
	public ImplUPT type() {
		return this.type;
	}
	
	@Override
	public ImplPCS contextFor(UUID playId) {
		ImplPCS ret = this.root.contextFor(playId);
		
		return ret != null && ret.subManager == this ? ret : null;
	}
	
	@Override
	public int currentLoad() {
		return this.current_load;
	}
}
