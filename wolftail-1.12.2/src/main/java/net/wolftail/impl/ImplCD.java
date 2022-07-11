package net.wolftail.impl;

import io.netty.buffer.ByteBuf;
import net.wolftail.util.tracker.ContentDiff;
import net.wolftail.util.tracker.PartialUniverse;
import net.wolftail.util.tracker.ContentOrder;

public final class ImplCD implements ContentDiff {
	
	private final ContentOrder order;
	
	private final ByteBuf data;
	
	public ImplCD(ContentOrder order, ByteBuf data) {
		this.order = order;
		
		this.data = data;
	}
	
	@Override
	public ContentOrder order() {
		return this.order;
	}
	
	@Override
	public ByteBuf toByteBuf() {
		return this.data.duplicate();
	}
	
	@Override
	public void apply(PartialUniverse dst) {
		ContentDiff.apply(this.toByteBuf(), dst);
	}
	
	@Override
	public int hashCode() {
		return this.order.hashCode() ^ this.data.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(o == null || o.getClass() != ImplCD.class) return false;
		
		ImplCD obj = (ImplCD) o;
		
		return obj.order.equals(this.order) && obj.data.equals(this.data);
	}
}
