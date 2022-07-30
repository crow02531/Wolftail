package net.wolftail.internal.tracker.container;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.wolftail.util.tracker.DiffVisitor;
import net.wolftail.util.tracker.Timing;

public class TimedTrackComplex<A> {
	
	private final Map<Timing, Row> rows;
	
	private final Supplier<A> factory;
	
	public TimedTrackComplex() {
		this(1, null);
	}
	
	public TimedTrackComplex(int capacity, Supplier<A> factory) {
		this.rows = new Object2ObjectArrayMap<>(capacity);
		
		this.factory = factory == null ? () -> null : factory;
	}
	
	public boolean add(Timing t, DiffVisitor dv) {
		Row r = null;
		
		for (Entry<Timing, Row> e : this.rows.entrySet()) {
			if (e.getValue().contains(dv))
				return false;
			
			if (e.getKey().equals(t))
				r = e.getValue();
		}
		
		if (r == null)
			this.rows.put(t, r = new Row());
		
		r._add(dv);
		return true;
	}
	
	public boolean remove(DiffVisitor dv) {
		Iterator<Entry<Timing, Row>> iter = this.rows.entrySet().iterator();
		
		while (iter.hasNext()) {
			Row r = iter.next().getValue();
			
			if (r._remove(dv)) {
				if (r.isEmpty())
					iter.remove();
				
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isEmpty() {
		return this.rows.isEmpty();
	}
	
	public void forEach(Consumer<Row> consumer) {
		this.rows.forEach((k, v) -> consumer.accept(v));
	}
	
	public void forEach(int tick, Consumer<Row> consumer) {
		this.rows.forEach((k, v) -> {
			if (k.match(tick))
				consumer.accept(v);
		});
	}
	
	public class Row extends TrackContainer<A> {
		
		private Row() {
			this.resetAttachment();
		}
		
		private boolean _add(DiffVisitor dv) {
			return super.add(dv);
		}
		
		private boolean _remove(DiffVisitor dv) {
			return super.remove(dv);
		}
		
		public void resetAttachment() {
			this.attachment = TimedTrackComplex.this.factory.get();
		}
		
		@Override
		public boolean add(DiffVisitor dv) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean remove(DiffVisitor dv) {
			throw new UnsupportedOperationException();
		}
	}
}
