package net.wolftail.api;

import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;

/**
 * The play context in client side.
 * 
 * @see PlayContext
 * @see ServerPlayContext
 */
@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_CLIENT)
public interface ClientPlayContext extends PlayContext {
	
	//no special ops now
}
