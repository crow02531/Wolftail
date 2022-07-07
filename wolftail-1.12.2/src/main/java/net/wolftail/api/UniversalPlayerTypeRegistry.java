package net.wolftail.api;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableBiMap;

import net.minecraft.util.ResourceLocation;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.impl.ImplUPTR;

public interface UniversalPlayerTypeRegistry {
	
	UniversalPlayerTypeRegistry INSTANCE = new ImplUPTR();
	
	@SideWith(section = GameSection.GAME_LOADING, thread = { LogicType.LOGIC_CLIENT, LogicType.LOGIC_SERVER })
	@Nonnull UniversalPlayerType register(@Nonnull ResourceLocation id, IServerEntryPoint entry_point_server, IClientEntryPoint entry_point_client, IClientFrameCallback callback_frame);
	
	@SideWith(section = GameSection.GAME_PREPARED)
	ResourceLocation idFor(UniversalPlayerType type);
	
	@SideWith(section = GameSection.GAME_LOADED)
	UniversalPlayerType registeredAt(ResourceLocation id);
	
	@SideWith(section = GameSection.GAME_LOADED)
	@Nonnull ImmutableBiMap<ResourceLocation, UniversalPlayerType> asMap();
}
