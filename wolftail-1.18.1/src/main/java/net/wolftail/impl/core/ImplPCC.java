package net.wolftail.impl.core;

import java.util.UUID;

import net.minecraft.network.Connection;

public final class ImplPCC extends ImplPC {
	
	final ImplUPT type;
	
	public ImplPCC(ImplUPT type, UUID id, String name, Connection connect) {
		super(id, name, connect);
		
		this.type = type;
	}

	@Override
	public ImplUPT playType() {
		return this.type;
	}
}
