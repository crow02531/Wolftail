package net.wolftail.impl.core;

import java.util.UUID;

import net.minecraft.network.Connection;

public final class ImplPCS extends ImplPC {
	
	final ImplMPCS subManager;
	
	public ImplPCS(ImplMPCS subManager, UUID id, String name, Connection connect) {
		super(id, name, connect);
		
		this.subManager = subManager;
	}
	
	@Override
	public ImplUPT playType() {
		return this.subManager.type;
	}
	
	public ImplMPCS subManager() {
		return this.subManager;
	}
}
