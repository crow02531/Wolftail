package net.wolftail.util.tracker;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Timing {
	
	private static final int CACHE_AMOUNT = 20;
	private static final Timing[] TIMING_CACHE = new Timing[CACHE_AMOUNT];
	
	static {
		for (int i = 0; i < CACHE_AMOUNT; ++i)
			TIMING_CACHE[i] = new Timing(0, i + 1);
	}
	
	public static final Timing EVERY_TICK = of(1);
	
	private int residue;
	private int modulo;
	
	public Timing(int tick, int interval) {
		if (interval <= 0)
			throw new IllegalArgumentException();
		
		this.residue = Math.abs(tick % (this.modulo = interval));
	}
	
	public int getRepresentative() {
		return this.residue;
	}
	
	public int getInterval() {
		return this.modulo;
	}
	
	public boolean match(int tick) {
		return tick % this.modulo == this.residue;
	}
	
	@Override
	public int hashCode() {
		return this.modulo ^ this.residue;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null || !(o instanceof Timing))
			return false;
		
		Timing o0 = (Timing) o;
		
		return this.residue == o0.residue && this.modulo == o0.modulo;
	}
	
	public static Timing of(int tick, int interval) {
		return tick == 0 ? of(interval) : new Timing(tick, interval);
	}
	
	public static Timing of(int interval) {
		if (interval <= 0)
			throw new IllegalArgumentException();
		
		return interval <= CACHE_AMOUNT ? TIMING_CACHE[interval - 1] : new Timing(0, interval);
	}
}
