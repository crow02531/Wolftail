package net.wolftail.api;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface SubPlayContextManager {
	
	RootPlayContextManager rootManager();
	
	UniversalPlayerType type();
	
	ServerPlayContext contextFor(UUID playId);
	
	int currentLoad();
	
	Map<UUID, ServerPlayContext> asMap();
	
	Set<ServerPlayContext> asSet();
}
