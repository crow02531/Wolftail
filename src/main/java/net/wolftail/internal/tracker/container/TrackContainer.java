package net.wolftail.internal.tracker.container;

import java.util.Set;

import com.google.common.collect.Sets;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.DimensionType;
import net.wolftail.util.tracker.DiffVisitor;

public class TrackContainer<A> {
	
	private MultiVisitor multi_a;
	private MultiVisitor multi_b;
	
	public A attachment;
	
	public DiffVisitor getMultiA() {
		return this.multi_a;
	}
	
	public DiffVisitor getMultiB() {
		return this.multi_b;
	}
	
	public void transferA2B() {
		if (this.multi_a == null)
			return;
		
		if (this.multi_b == null)
			this.multi_b = this.multi_a;
		else
			this.multi_b.visitors.addAll(this.multi_a.visitors);
		
		this.multi_a = null;
	}
	
	public void transferB2A() {
		if (this.multi_b == null)
			return;
		
		if (this.multi_a == null)
			this.multi_a = this.multi_b;
		else
			this.multi_a.visitors.addAll(this.multi_b.visitors);
		
		this.multi_b = null;
	}
	
	public boolean isEmpty() {
		return this.multi_a == null && this.multi_b == null;
	}
	
	public boolean contains(DiffVisitor dv) {
		if (this.multi_a != null && this.multi_a.visitors.contains(dv))
			return true;
		if (this.multi_b != null && this.multi_b.visitors.contains(dv))
			return true;
		
		return false;
	}
	
	public boolean add(DiffVisitor dv) {
		if (this.multi_a == null)
			this.multi_a = new MultiVisitor();
		
		return this.multi_a.visitors.add(dv);
	}
	
	public boolean remove(DiffVisitor dv) {
		Set<DiffVisitor> vs;
		
		if (this.multi_a != null && (vs = this.multi_a.visitors).remove(dv)) {
			if (vs.isEmpty())
				this.multi_a = null;
			
			return true;
		}
		
		if (this.multi_b != null && (vs = this.multi_b.visitors).remove(dv)) {
			if (vs.isEmpty())
				this.multi_b = null;
			
			return true;
		}
		
		return false;
	}
	
	private static final class MultiVisitor implements DiffVisitor {
		
		final Set<DiffVisitor> visitors = Sets.newIdentityHashSet();
		
		@Override
		public void jzBegin() {
			this.visitors.forEach(DiffVisitor::jzBegin);
		}
		
		@Override
		public void jzEnd() {
			this.visitors.forEach(DiffVisitor::jzEnd);
		}
		
		@Override
		public void jzBindWorld(DimensionType dim) {
			this.visitors.forEach(v -> v.jzBindWorld(dim));
		}
		
		@Override
		public void jzBindChunk(int chunkX, int chunkZ) {
			this.visitors.forEach(v -> v.jzBindChunk(chunkX, chunkZ));
		}
		
		@Override
		public void jzBindBlock(short index) {
			this.visitors.forEach(v -> v.jzBindBlock(index));
		}
		
		@Override
		public void jzUnbindWorld() {
			this.visitors.forEach(DiffVisitor::jzUnbindWorld);
		}
		
		@Override
		public void jzUnbindChunk() {
			this.visitors.forEach(DiffVisitor::jzUnbindChunk);
		}
		
		@Override
		public void jzUnbindBlock() {
			this.visitors.forEach(DiffVisitor::jzUnbindBlock);
		}
		
		@Override
		public void jzSetDaytime(int daytime) {
			this.visitors.forEach(v -> v.jzSetDaytime(daytime));
		}
		
		@Override
		public void jzSetWeather(float rainStr, float thunderStr) {
			this.visitors.forEach(v -> v.jzSetWeather(rainStr, thunderStr));
		}
		
		@Override
		public void jzSetSection(int index, ByteBuf buf) {
			if (buf == null)
				this.visitors.forEach(v -> v.jzSetSection(index, null));
			else
				this.visitors.forEach(v -> v.jzSetSection(index, buf.duplicate()));
		}
		
		@Override
		public void jzSetState(IBlockState state) {
			this.visitors.forEach(v -> v.jzSetState(state));
		}
		
		@Override
		public void jzSetTileEntity(ByteBuf buf) {
			if (buf == null)
				this.visitors.forEach(v -> v.jzSetTileEntity(null));
			else
				this.visitors.forEach(v -> v.jzSetTileEntity(buf.duplicate()));
		}
	}
}
