package net.wolftail.api;

import javax.annotation.Nonnull;

import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;

@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_SERVER)
public interface IServerListener {
	
	/**
	 * Called when a play context just setup.
	 * 
	 * @param context	the new play context
	 */
	void onEnter(@Nonnull PlayContext context);
	
	/**
	 * Called when a play context leave the game.
	 * 
	 * @param context	the leaving play context
	 */
	void onLeave(@Nonnull PlayContext context);
}
