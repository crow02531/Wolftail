package net.wolftail.api;

import java.util.Random;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableBiMap;

import net.minecraft.util.ResourceLocation;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.Sealed;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.internal.core.ImplUPTR;

@Sealed
@SideWith(section = GameSection.GAME_LOADED)
public interface UniversalPlayerTypeRegistry {
	
	UniversalPlayerTypeRegistry INSTANCE = new ImplUPTR();
	
	@SideWith(section = GameSection.GAME_LOADING, thread = LogicType.LOGIC_HOST)
	@Nonnull
	UniversalPlayerType register(@Nonnull ResourceLocation id, IServerHandler serverHandler,
			IClientHandler clientHandler);
	
	@SideWith(section = GameSection.GAME_PREPARED)
	ResourceLocation idFor(UniversalPlayerType type);
	
	UniversalPlayerType byId(ResourceLocation id);
	
	@Nonnull
	UniversalPlayerType getRandomType(Random rnd);
	
	@Nonnull
	ImmutableBiMap<ResourceLocation, UniversalPlayerType> asMap();
}
