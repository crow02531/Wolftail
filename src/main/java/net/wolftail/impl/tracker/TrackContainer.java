package net.wolftail.impl.tracker;

import java.util.function.Consumer;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.DimensionType;
import net.wolftail.impl.util.collect.Strategies;
import net.wolftail.util.tracker.DiffVisitor;
import net.wolftail.util.tracker.Timing;

public final class TrackContainer<A> {
	
	private final Object2ObjectMap<Timing, Row> rows;
	
	public TrackContainer() {
		this(1);
	}
	
	public TrackContainer(int capacity) {
		this.rows = new Object2ObjectArrayMap<>(capacity);
	}
	
	public boolean add(Timing t, DiffVisitor dv) {
		Row r = null;
		
		for(Entry<Timing, Row> e : this.rows.object2ObjectEntrySet()) {
			if(e.getValue().contains(dv))
				return false;
			
			if(e.getKey().equals(t))
				r = e.getValue();
		}
		
		if(r == null)
			this.rows.put(t, r = new Row());
		
		r.join(dv);
		return true;
	}
	
	public boolean remove(DiffVisitor dv) {
		ObjectIterator<Entry<Timing, Row>> iter = this.rows.object2ObjectEntrySet().iterator();
		
		while(iter.hasNext()) {
			Row r = iter.next().getValue();
			
			if(r.tryLeave(dv)) {
				if(r.set_a == null && r.set_b == null) iter.remove();
				
				return true;
			}
		}
		
		return false;
	}
	
	public void forEach(Consumer<Row> consumer) {
		this.rows.values().forEach(consumer);
	}
	
	public void forEach(int tick, Consumer<Row> consumer) {
		this.rows.object2ObjectEntrySet().forEach(e -> {
			if(e.getKey().match(tick)) consumer.accept(e.getValue());
		});
	}
	
	public class Row {
		
		private MultiVisitor set_a;
		private MultiVisitor set_b;
		
		public A attachment;
		
		private Row() {}
		
		public DiffVisitor getMultiA() {
			return this.set_a;
		}
		
		public DiffVisitor getMultiB() {
			return this.set_b;
		}
		
		public void transferA2B() {
			if(this.set_a == null) return;
			
			if(this.set_b == null) this.set_b = this.set_a;
			else this.set_b.visitors.addAll(this.set_a.visitors);
			
			this.set_a = null;
		}
		
		public void transferB2A() {
			if(this.set_b == null) return;
			
			if(this.set_a == null) this.set_a = this.set_b;
			else this.set_a.visitors.addAll(this.set_b.visitors);
			
			this.set_b = null;
		}
		
		private boolean contains(DiffVisitor dv) {
			if(this.set_a != null && this.set_a.visitors.contains(dv))
				return true;
			if(this.set_b != null && this.set_b.visitors.contains(dv))
				return true;
			
			return false;
		}
		
		private void join(DiffVisitor dv) {
			if(this.set_a == null)
				this.set_a = new MultiVisitor();
			
			this.set_a.visitors.add(dv);
		}
		
		private boolean tryLeave(DiffVisitor dv) {
			ObjectSet<DiffVisitor> vs;
			
			if(this.set_a != null && (vs = this.set_a.visitors).remove(dv)) {
				if(vs.isEmpty()) this.set_a = null;
				
				return true;
			}
			
			if(this.set_b != null && (vs = this.set_b.visitors).remove(dv)) {
				if(vs.isEmpty()) this.set_b = null;
				
				return true;
			}
			
			return false;
		}
	}
	
	private static class MultiVisitor implements DiffVisitor {
		
		final ObjectSet<DiffVisitor> visitors = new ObjectOpenCustomHashSet<>(Strategies.identity());
		
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
		public void jzSetDaytime(long daytime) {
			this.visitors.forEach(v -> v.jzSetDaytime(daytime));
		}
		
		@Override
		public void jzSetWeather(float rainStr, float thunderStr) {
			this.visitors.forEach(v -> v.jzSetWeather(rainStr, thunderStr));
		}
		
		@Override
		public void jzSetSection(int index, ByteBuf buf) {
			this.visitors.forEach(v -> v.jzSetSection(index, buf.duplicate()));
		}
		
		@Override
		public void jzSetState(IBlockState state) {
			this.visitors.forEach(v -> v.jzSetState(state));
		}
		
		@Override
		public void jzSetTileEntity(ByteBuf buf) {
			this.visitors.forEach(v -> v.jzSetTileEntity(buf.duplicate()));
		}
	}
}
