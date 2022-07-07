package net.wolftail.api;

import javax.annotation.Nonnull;

import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;

@SideWith(thread = LogicType.LOGIC_CLIENT)
public interface IClientFrameCallback {
	
	@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_CLIENT)
	void onFrame(@Nonnull ClientPlayContext context);
}
