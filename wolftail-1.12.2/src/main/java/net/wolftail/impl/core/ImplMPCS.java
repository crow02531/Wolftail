package net.wolftail.impl.core;

import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Sets;

import net.wolftail.api.ServerPlayContext;
import net.wolftail.api.SubPlayContextManager;

public final class ImplMPCS implements SubPlayContextManager {
	
	final ImplMPCR root;
	final ImplUPT type;
	
	int current_load;
	
	private Set<ServerPlayContext> view_set;
	
	public ImplMPCS(ImplUPT arg0, ImplMPCR arg1) {
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
	public ImplPC.Server contextFor(UUID playId) {
		ImplPC.Server ret = this.root.contextFor(playId);
		
		return ret != null && ret.manager == this ? ret : null;
	}
	
	@Override
	public int currentLoad() {
		return this.current_load;
	}
	
	@Override
	public Set<ServerPlayContext> asSet() {
		Set<ServerPlayContext> ret = this.view_set;
		
		return ret != null ? ret : (this.view_set = Sets.filter(this.root.asContextSet(), val -> val.manager() == this));
	}
}
