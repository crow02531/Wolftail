package net.wolftail.impl;

import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Sets;

import net.wolftail.api.RootPlayContextManager;
import net.wolftail.api.ServerPlayContext;
import net.wolftail.api.SubPlayContextManager;
import net.wolftail.api.UniversalPlayerType;

public final class ImplMPCSub implements SubPlayContextManager {
	
	final ImplMPCRoot root;
	final ImplUPT type;
	
	int current_load;
	
	private Set<ServerPlayContext> view_set;
	
	public ImplMPCSub(ImplUPT arg0, ImplMPCRoot arg1) {
		this.type = arg0;
		this.root = arg1;
	}
	
	@Override
	public RootPlayContextManager rootManager() {
		return this.root;
	}
	
	@Override
	public UniversalPlayerType type() {
		return this.type;
	}
	
	@Override
	public ServerPlayContext contextFor(UUID playId) {
		ImplPCServer ret = this.root.contextFor(playId);
		
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
