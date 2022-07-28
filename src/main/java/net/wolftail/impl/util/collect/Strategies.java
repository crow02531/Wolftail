package net.wolftail.impl.util.collect;

import it.unimi.dsi.fastutil.Hash.Strategy;

public final class Strategies {
	
	private Strategies() {}
	
	@SuppressWarnings("unchecked")
	public static <T> Strategy<T> identity() {
		return (Strategy<T>) IDENTITY_STRATEGY;
	}
	
	private static final Strategy<Object> IDENTITY_STRATEGY = new Strategy<Object>() {
		
		@Override
		public int hashCode(Object o) {
			return System.identityHashCode(o);
		}
		
		@Override
		public boolean equals(Object a, Object b) {
			return a == b;
		}
	};
}
