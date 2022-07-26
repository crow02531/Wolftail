package net.wolftail.api;

import javax.annotation.Nonnull;

import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;

/**
 * The play context in server side.
 * 
 * @see PlayContext
 * @see ClientPlayContext
 */
@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_SERVER)
public interface ServerPlayContext extends PlayContext {
	
	@Nonnull SubPlayContextManager manager();
}
