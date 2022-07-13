package net.wolftail.impl;

import com.google.common.collect.ImmutableSet;

import io.netty.buffer.ByteBuf;
import net.wolftail.util.tracker.ContentDiff;
import net.wolftail.util.tracker.ContentOrder;
import net.wolftail.util.tracker.SlaveUniverse;

public final class ImplCD implements ContentDiff {
	
	private final ImmutableSet<ContentOrder> orders;
	
	private final ByteBuf data;
	
	public ImplCD(ImmutableSet<ContentOrder> orders, ByteBuf content_diff) {
		this.orders = orders;
		
		this.data = content_diff;
	}
	
	@Override
	public ImmutableSet<ContentOrder> orders() {
		return this.orders;
	}
	
	@Override
	public ByteBuf toByteBuf() {
		return this.data.duplicate();
	}
	
	@Override
	public void apply(SlaveUniverse dst) {
		ContentDiff.apply(this.toByteBuf(), dst);
	}
	
	@Override
	public int hashCode() {
		return this.orders.hashCode() ^ this.data.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(o == null || !(o instanceof ImplCD)) return false;
		
		ImplCD obj = (ImplCD) o;
		
		return obj.orders.equals(this.orders) && obj.data.equals(this.data);
	}
}
