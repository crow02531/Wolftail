package net.wolftail.api;

import java.util.Random;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableBiMap;

import net.minecraft.resources.ResourceLocation;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.impl.core.ImplUPTR;

public interface UniversalPlayerTypeRegistry {
	
	UniversalPlayerTypeRegistry INSTANCE = new ImplUPTR();
	
	@SideWith(section = GameSection.GAME_LOADING, thread = LogicType.LOGIC_HOST)
	@Nonnull UniversalPlayerType register(@Nonnull ResourceLocation id,
			IEntryPoint serverEntryPoint,
			IEntryPoint clientEntryPoint);
	
	@SideWith(section = GameSection.GAME_PREPARED)
	ResourceLocation idFor(UniversalPlayerType type);
	
	@SideWith(section = GameSection.GAME_LOADED)
	UniversalPlayerType registeredAt(ResourceLocation id);
	
	@SideWith(section = GameSection.GAME_LOADED)
	@Nonnull UniversalPlayerType getRandomType(Random rnd);
	
	@SideWith(section = GameSection.GAME_LOADED)
	@Nonnull ImmutableBiMap<ResourceLocation, UniversalPlayerType> asMap();
}
