package net.wolftail.api.lifecycle;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public abstract class GameSection {
	
	protected final SectionToken token;
	
	final ReentrantReadWriteLock lock;
	
	SectionState state;
	
	protected GameSection() {
		this(SectionState.BEFORE);
	}
	
	protected GameSection(SectionState initial) {
		this.token = new SectionToken(this);
		
		this.lock = new ReentrantReadWriteLock();
		
		this.state = Objects.requireNonNull(initial);
	}
	
	public final void verify(SectionToken token) {
		if(this.token != Objects.requireNonNull(token))
			throw new SecurityException();
	}
	
	public final void ensure(SectionState intended, Runnable action) {
		Objects.requireNonNull(intended, "intended");
		Objects.requireNonNull(action, "action");
		
		this.block((current) -> {
			if(intended != current)
				throw new IllegalStateException("Not in " + intended + " state but " + current);
			
			action.run();
		});
	}
	
	public final void block(Consumer<SectionState> action) {
		Lock rlock = this.lock.readLock();
		
		rlock.lock();
		
		try {
			action.accept(this.state);
		} finally {
			rlock.unlock();
		}
	}
}
