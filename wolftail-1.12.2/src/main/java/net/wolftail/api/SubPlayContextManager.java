package net.wolftail.api;

import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;

import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;

/**
 * A context manager governing a specific uniplayer type.
 * 
 * @see RootPlayContextManager
 */
@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_SERVER)
public interface SubPlayContextManager {
	
	@Nonnull RootPlayContextManager rootManager();
	
	@Nonnull UniversalPlayerType type();
	
	ServerPlayContext contextFor(UUID playId);
	
	int currentLoad();
	
	/**
	 * View the manager as a set of playing contexts.
	 * 
	 * @return an unmodifiable set, changes in the manager
	 * 		are reflected
	 */
	@Nonnull Set<ServerPlayContext> asSet();
}
