package net.wolftail.impl.network;

import net.minecraft.network.INetHandler;
import net.minecraft.util.text.ITextComponent;

public final class NoopNetHandler implements INetHandler {
	
	@Override
	public void onDisconnect(ITextComponent reason) {
		//NOOP
	}
}
