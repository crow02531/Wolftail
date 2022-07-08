package net.wolftail.impl;

import net.minecraft.network.PacketBuffer;
import net.wolftail.util.tracker.ContentDiff;
import net.wolftail.util.tracker.SubscribeOrder;

public final class ImplCD implements ContentDiff {
	
	public ImplCD(SubscribeOrder order) {
		
	}
	
	@Override
	public SubscribeOrder order() {
		return null;
	}
	
	@Override
	public void writeTo(PacketBuffer buf) {
		
	}
}
