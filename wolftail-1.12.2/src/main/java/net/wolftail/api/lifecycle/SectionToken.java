package net.wolftail.api.lifecycle;

public final class SectionToken {
	
	private final GameSection section;
	
	SectionToken(GameSection gs) {
		this.section = gs;
	}
	
	public boolean isFor(GameSection section) {
		return section == this.section;
	}
	
	public SectionState currentState() {
		LogicType.ensureHost();
		
		return this.section.state;
	}
	
	public void doLock() {
		LogicType.ensureHost();
		
		this.section.lock.writeLock().lock();
	}
	
	public void doAdvance() {
		LogicType.ensureHost();
		
		GameSection section = this.section;
		
		if(!section.lock.isWriteLockedByCurrentThread())
			throw new IllegalThreadStateException();
		
		section.state = section.state.advance();
	}
	
	public void doUnlock() {
		LogicType.ensureHost();
		
		this.section.lock.writeLock().unlock();
	}
}
