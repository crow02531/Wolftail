package net.wolftail.api;

import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraft.server.MinecraftServer;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.impl.SharedImpls;

@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_SERVER)
public interface RootPlayContextManager {
	
	@Nonnull MinecraftServer server();
	
	ServerPlayContext contextFor(UUID playId);
	
	int currentLoad();
	
	int maxLoad();
	
	SubPlayContextManager subManager(UniversalPlayerType type);
	
	@Nonnull Set<SubPlayContextManager> asManagerSet();
	
	@Nonnull Set<ServerPlayContext> asContextSet();
	
	@Nonnull
	static RootPlayContextManager instanceFor(@Nonnull MinecraftServer server) {
		return SharedImpls.as(server).wolftail_getRootManager();
	}
}
