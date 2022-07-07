package net.wolftail.api;

import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;

import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;

@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_SERVER)
public interface SubPlayContextManager {
	
	@Nonnull RootPlayContextManager rootManager();
	
	@Nonnull UniversalPlayerType type();
	
	ServerPlayContext contextFor(UUID playId);
	
	int currentLoad();
	
	@Nonnull Set<ServerPlayContext> asSet();
}
