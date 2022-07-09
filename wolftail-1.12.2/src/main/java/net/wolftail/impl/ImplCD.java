package net.wolftail.impl;

import io.netty.buffer.ByteBuf;
import net.wolftail.util.tracker.ContentDiff;
import net.wolftail.util.tracker.PartialUniverse;
import net.wolftail.util.tracker.SubscribeOrder;

public final class ImplCD implements ContentDiff {
	
	private final SubscribeOrder order;
	
	private final ByteBuf data;
	
	public ImplCD(SubscribeOrder order, ByteBuf data) {
		this.order = order;
		
		this.data = data;
	}
	
	@Override
	public SubscribeOrder order() {
		return this.order;
	}
	
	@Override
	public ByteBuf asByteBuf() {
		return this.data.duplicate();
	}
	
	@Override
	public void apply(PartialUniverse dst) {
		ContentDiff.apply(this.asByteBuf(), dst);
	}
}
