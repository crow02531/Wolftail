package net.wolftail.api;

import java.util.Map;
import java.util.Set;

import net.minecraft.util.ResourceLocation;
import net.wolftail.impl.ImplUPTR;

public interface UniversalPlayerTypeRegistry {
	
	UniversalPlayerTypeRegistry INSTANCE = new ImplUPTR();
	
	UniversalPlayerType register(ResourceLocation id, UniversalPlayerType type);
	
	UniversalPlayerType registeredAt(ResourceLocation id);
	
	ResourceLocation idFor(UniversalPlayerType type);
	
	Map<ResourceLocation, UniversalPlayerType> asMap();
	
	Set<UniversalPlayerType> asSet();
}
