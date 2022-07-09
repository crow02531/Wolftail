package net.wolftail.api.lifecycle;

import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import net.wolftail.impl.SharedImpls.H1;

public enum GameSection {
	
	GAME_PREPARING(H1.TOKEN_PREPARING), 
	GAME_PREPARED(H1.TOKEN_PREPARED), 
	GAME_LOADING(H1.TOKEN_LOADING), 
	GAME_LOADED(H1.TOKEN_LOADED), 
	GAME_WANDERING(H1.TOKEN_WANDERING), 
	GAME_PLAYING(H1.TOKEN_PLAYING);
	
	private final H1 token;
	
	private GameSection(H1 t) {
		this.token = t;
	}
	
	public final void ensure(@Nonnull SectionState intended, @Nonnull Runnable action) {
		this.block((current) -> {
			if(intended != current)
				throw new IllegalStateException("Not in " + intended + " state but " + current);
			
			action.run();
		});
	}
	
	public final void block(@Nonnull Consumer<SectionState> action) {
		Lock rlock = this.token.lock.readLock();
		
		rlock.lock();
		
		try {
			action.accept(this.token.state);
		} finally {
			rlock.unlock();
		}
	}
}
