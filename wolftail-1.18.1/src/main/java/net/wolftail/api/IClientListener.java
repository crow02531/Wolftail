package net.wolftail.api;

import javax.annotation.Nonnull;

import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;

@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_CLIENT)
public interface IClientListener {
	
	/**
	 * Called when game section changes from WANDERING to PLAYING.
	 * 
	 * @param context	the playing play context
	 */
	void onEnter(@Nonnull PlayContext context);
	
	/**
	 * Called every game loop.
	 */
	void onRender();
	
	/**
	 * Called when game section changes from PLAYING to WANDERING.
	 */
	void onLeave();
}
