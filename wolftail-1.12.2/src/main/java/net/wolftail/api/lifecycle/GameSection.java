package net.wolftail.api.lifecycle;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import net.wolftail.impl.SharedImpls;

public enum GameSection {
	
	GAME_PREPARING, 
	GAME_PREPARED, 
	GAME_LOADING, 
	GAME_LOADED, 
	GAME_WANDERING, 
	GAME_PLAYING;
	
	private final ReentrantReadWriteLock lock;
	
	private SectionState state;
	
	private GameSection() {
		this.lock = new ReentrantReadWriteLock();
		
		this.state = SectionState.BEFORE;
	}
	
	public final void ensure(@Nonnull SectionState intended, @Nonnull Runnable action) {
		this.block((current) -> {
			if(intended != current)
				throw new IllegalStateException("Not in " + intended + " state but " + current);
			
			action.run();
		});
	}
	
	public final void block(@Nonnull Consumer<SectionState> action) {
		Lock rlock = this.lock.readLock();
		
		rlock.lock();
		
		try {
			action.accept(this.state);
		} finally {
			rlock.unlock();
		}
	}
	
	static {
		SharedImpls.H1.token_preparing = new Token(GAME_PREPARING);
		SharedImpls.H1.token_prepared = new Token(GAME_PREPARED);
		SharedImpls.H1.token_loading = new Token(GAME_LOADING);
		SharedImpls.H1.token_loaded = new Token(GAME_LOADED);
		SharedImpls.H1.token_wandering = new Token(GAME_WANDERING);
		SharedImpls.H1.token_playing = new Token(GAME_PLAYING);
	}
	
	private static final class Token extends SharedImpls.H1 {
		
		private final GameSection target;
		
		private Token(GameSection target) {
			this.target = target;
		}
		
		@Override
		public void doLock() {
			this.target.lock.writeLock().lock();
		}
		
		@Override
		public void doAdvance() {
			this.target.state = this.target.state.advance();
		}
		
		@Override
		public void doUnlock() {
			this.target.lock.writeLock().unlock();
		}
		
		@Override
		public SectionState currentState() {
			return this.target.state;
		}
	}
}
