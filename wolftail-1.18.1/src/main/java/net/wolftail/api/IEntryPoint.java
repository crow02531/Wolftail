package net.wolftail.api;

import javax.annotation.Nonnull;

import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;

public interface IEntryPoint {
	
	/**
	 * Called when a play context just setup.
	 * 
	 * @param context	the play context
	 */
	@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_HOST)
	void onEnter(@Nonnull PlayContext context);
}
