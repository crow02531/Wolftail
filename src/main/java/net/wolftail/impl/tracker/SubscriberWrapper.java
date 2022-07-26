package net.wolftail.impl.tracker;

import java.util.function.Consumer;

import net.wolftail.util.tracker.ContentDiff;

public final class SubscriberWrapper {
	
	private final Consumer<ContentDiff> wrapped;
	
	private int counter;
	
	private final DiffWriter writer;
	
	public SubscriberWrapper(Consumer<ContentDiff> subscriber) {
		this.wrapped = subscriber;
		
		this.writer = new DiffWriter();
	}
	
	public void onSubscribe() {
		this.counter++;
	}
	
	public boolean onUnsubscribe() {
		return --this.counter == 0;
	}
	
	public DiffWriter getWriter() {
		return this.writer;
	}
	
	public void dispatch() {
		ContentDiff diff = this.writer.harvest();
		
		if(diff != null)
			this.wrapped.accept(diff);
	}
}
