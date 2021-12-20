package net.wolftail.api;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.server.MinecraftServer;
import net.wolftail.impl.SharedImpls;

public interface RootPlayContextManager {
	
	MinecraftServer server();
	
	ServerPlayContext contextFor(UUID playId);
	
	int currentLoad();
	
	int maxLoad();
	
	SubPlayContextManager subManager(UniversalPlayerType type);
	
	Map<UniversalPlayerType, SubPlayContextManager> asManagerMap();
	
	Set<SubPlayContextManager> asManagerSet();
	
	Map<UUID, ServerPlayContext> asContextMap();
	
	Set<ServerPlayContext> asContextSet();
	
	static RootPlayContextManager managerFor(MinecraftServer server) {
		return SharedImpls.as(server).wolftail_getRootManager();
	}
}
