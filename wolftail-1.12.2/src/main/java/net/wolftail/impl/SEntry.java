package net.wolftail.impl;

import java.util.function.Consumer;

import net.wolftail.util.tracker.ContentDiff;

public final class SEntry {
	
	public Consumer<ContentDiff> subscriber;
	public boolean initial;
	
	public SEntry(Consumer<ContentDiff> arg) {
		this.subscriber = arg;
		this.initial = true;
	}
	
	@Override
	public int hashCode() {
		return System.identityHashCode(this.subscriber);
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.subscriber == ((SEntry) obj).subscriber;
	}
}
