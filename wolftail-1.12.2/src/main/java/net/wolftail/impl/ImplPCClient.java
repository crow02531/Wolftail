package net.wolftail.impl;

import java.util.UUID;

import net.minecraft.network.NetworkManager;
import net.wolftail.api.ClientPlayContext;
import net.wolftail.api.UniversalPlayerType;

public final class ImplPCClient extends ImplPC implements ClientPlayContext {
	
	final UniversalPlayerType type;
	
	private boolean firstFrame = true;
	
	public ImplPCClient(UniversalPlayerType arg0, UUID arg1, String arg2, NetworkManager arg3) {
		super(arg1, arg2, arg3);
		
		this.type = arg0;
	}
	
	@Override
	public UniversalPlayerType playType() {
		return this.type;
	}
	
	public NetworkManager getConnection() {
		return this.connection;
	}
	
	public boolean checkFirstFrameAndClear() {
		if(this.firstFrame) {
			this.firstFrame = false;
			
			return true;
		}
		
		return false;
	}
}
