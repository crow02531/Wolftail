package net.wolftail.api.lifecycle;

import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import net.wolftail.impl.SharedImpls.H1;

/**
 * Minecraft's lifecycle could be divided into server sections.
 * 
 * <h3>{@link PhysicalType#DEDICATED_SERVER Dedicated Server}</h3>
 * 
 * <pre>
 *   +-----------+---------+-----------+-----------------------+
 *   |           |                PREPARED                     |
 *   |                     |               LOADED              |
 *   | PREPARING | LOADING | WANDERING |        PLAYING        |
 *   +-----------+---------+-----------+-----------------------+
 * </pre>
 * 
 * <h3>{@link PhysicalType#INTEGRATED_CLIENT Integrated Client}</h3>
 * 
 * <pre>
 *   +-----------+---------+-----------+---------+-----------+---------+-----+
 *   |           |                         PREPARED                          |
 *   |                     |                      LOADED                     |
 *   | PREPARING | LOADING | WANDERING | PLAYING | WANDERING | PLAYING | ... |
 *   +-----------+---------+-----------+---------+-----------+---------+-----+
 * </pre>
 * 
 * @see PhysicalType
 */
public enum GameSection {
	
	/**
	 * The very early stage, during which lunchwrapper initializes tweakers.
	 */
	GAME_PREPARING(H1.TOKEN_PREPARING),
	
	GAME_PREPARED(H1.TOKEN_PREPARED),
	
	/**
	 * The stage where game content registering occurs.
	 */
	GAME_LOADING(H1.TOKEN_LOADING),
	
	GAME_LOADED(H1.TOKEN_LOADED),
	
	/**
	 * In dedicated server this stage takes a very short period and the
	 * application simply do nothing. However in client this stage means loaded
	 * but not in playing, such as the time you are facing main menu.
	 */
	GAME_WANDERING(H1.TOKEN_WANDERING),
	
	/**
	 * The playing stage.
	 */
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
