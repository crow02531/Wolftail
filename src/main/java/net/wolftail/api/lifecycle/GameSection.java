package net.wolftail.api.lifecycle;

import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import net.wolftail.impl.core.SectionHandler;

/**
 * Minecraft's lifecycle could be divided into several sections.
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
	GAME_PREPARING(SectionHandler.HANDLER_PREPARING),
	
	GAME_PREPARED(SectionHandler.HANDLER_PREPARED),
	
	/**
	 * The stage where game content registering occurs.
	 */
	GAME_LOADING(SectionHandler.HANDLER_LOADING),
	
	GAME_LOADED(SectionHandler.HANDLER_LOADED),
	
	/**
	 * In dedicated server this stage takes a very short period and the
	 * application simply do nothing. However in client this stage means loaded
	 * but not in playing, such as the time you are facing main menu.
	 */
	GAME_WANDERING(SectionHandler.HANDLER_WANDERING),
	
	/**
	 * The playing stage.
	 */
	GAME_PLAYING(SectionHandler.HANDLER_PLAYING);
	
	private final SectionHandler handler;
	
	private GameSection(SectionHandler h) {
		this.handler = h;
	}
	
	/**
	 * Ensure {@code this}'s state equals to {@code intended} during the {@code action}.
	 * 
	 * @param intended	the desiring state
	 * @param action	the action to be executed
	 * 
	 * @throws IllegalStateException	when the current state of {@code this} dosen't
	 * 		equals to {@code intended}
	 */
	public void ensure(@Nonnull SectionState intended, @Nonnull Runnable action) {
		this.block((current) -> {
			if(intended != current)
				throw new IllegalStateException("Not in " + intended + " state but " + current);
			
			action.run();
		});
	}
	
	/**
	 * Block the game section change progress so that game section remains
	 * the same during the {@code action}.
	 * 
	 * @param action	the action to be executed, consuming the current
	 * 		state of {@code this}
	 */
	public void block(@Nonnull Consumer<SectionState> action) {
		Lock rlock = this.handler.getLock();
		
		rlock.lock();
		
		try {
			action.accept(this.handler.getState());
		} finally {
			rlock.unlock();
		}
	}
}
