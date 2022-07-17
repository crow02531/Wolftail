package net.wolftail.api;

import javax.annotation.Nonnull;

import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;

@SideWith(thread = LogicType.LOGIC_SERVER)
public interface IServerEntryPoint {
	
	/**
	 * Called when the server context just setup. You can set a
	 * tickable net handler so that get call every server tick.
	 * 
	 * @param context	the playing context
	 */
	@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_SERVER)
	void onEnter(@Nonnull ServerPlayContext context);
}
