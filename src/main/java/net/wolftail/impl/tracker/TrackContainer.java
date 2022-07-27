package net.wolftail.impl.tracker;

import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
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
		Entry entry = new Entry(dv);
		Row r = null;
		
		for(Object2ObjectMap.Entry<Timing, Row> e : this.rows.object2ObjectEntrySet()) {
			if(e.getValue().entries.contains(entry))
				return false;
			
			if(e.getKey().equals(t))
				r = e.getValue();
		}
		
		if(r == null)
			this.rows.put(t, r = new Row());
		
		r.entries.add(entry);
		return true;
	}
	
	public boolean remove(DiffVisitor dv) {
		Entry prob = new Entry(dv);
		
		ObjectIterator<Object2ObjectMap.Entry<Timing, Row>> iter = this.rows.object2ObjectEntrySet().iterator();
		while(iter.hasNext()) {
			HashSet<Entry> entries = iter.next().getValue().entries;
			
			if(entries.remove(prob)) {
				if(entries.size() == 0) iter.remove();
				
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
		
		private final HashSet<Entry> entries = new HashSet<>();
		
		public A attachment;
		
		public void assemble(BiConsumer<DiffVisitor, Boolean> consumer) {
			this.entries.forEach(e -> { consumer.accept(e.dv, e.initial); e.initial = false; });
		}
	}
	
	private static class Entry {
		
		final DiffVisitor dv;
		
		boolean initial;
		
		Entry(DiffVisitor dv) {
			this.dv = dv;
			
			this.initial = true;
		}
		
		@Override
		public int hashCode() {
			return System.identityHashCode(this.dv);
		}
		
		@Override
		public boolean equals(Object o) {
			return this.dv == ((Entry) o).dv;
		}
	}
}
