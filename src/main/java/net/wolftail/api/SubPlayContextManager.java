package net.wolftail.api;

import java.util.UUID;

import javax.annotation.Nonnull;

import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.Sealed;
import net.wolftail.api.lifecycle.SideWith;

/**
 * A context manager governing a specific uniplayer type.
 * 
 * @see RootPlayContextManager
 */
@Sealed
@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_SERVER)
public interface SubPlayContextManager {
	
	@Nonnull
	RootPlayContextManager rootManager();
	
	@Nonnull
	UniversalPlayerType type();
	
	PlayContext contextFor(UUID playId);
	
	int currentLoad();
}
