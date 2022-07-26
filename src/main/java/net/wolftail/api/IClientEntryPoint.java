package net.wolftail.api;

import javax.annotation.Nonnull;

import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;

@SideWith(thread = LogicType.LOGIC_CLIENT)
public interface IClientEntryPoint {
	
	/**
	 * Called when client just enters playing section. You can set a
	 * tickable net handler so that get call every client tick.
	 * 
	 * @param context	the playing context
	 */
	@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_CLIENT)
	void onEnter(@Nonnull ClientPlayContext context);
}
