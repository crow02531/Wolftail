package net.wolftail.impl.core;

import java.util.Random;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;

import net.minecraft.util.ResourceLocation;
import net.wolftail.api.IClientHandler;
import net.wolftail.api.IServerHandler;
import net.wolftail.api.UniversalPlayerType;
import net.wolftail.api.UniversalPlayerTypeRegistry;

public final class ImplUPTR implements UniversalPlayerTypeRegistry {
	
	private BiMap<ResourceLocation, UniversalPlayerType> underlying;
	
	public ImplUPTR() {
		this.underlying = HashBiMap.create();
		
		this.underlying.put(UniversalPlayerType.TYPE_PLAYER_ID, UniversalPlayerType.TYPE_PLAYER);
	}
	
	@Override
	public UniversalPlayerType register(ResourceLocation id, IServerHandler s, IClientHandler c) {
		ImplUPT r = new ImplUPT(s, c);
		
		if(this.underlying.putIfAbsent(id, r) != null)
			throw new IllegalStateException("The ID " + id + " had been registered");
		
		return r;
	}
	
	@Override
	public ResourceLocation idFor(UniversalPlayerType type) {
		return this.underlying.inverse().get(type);
	}
	
	@Override
	public UniversalPlayerType byId(ResourceLocation id) {
		return this.underlying.get(id);
	}
	
	@Override
	public UniversalPlayerType getRandomType(Random rnd) {
		if(rnd == null) rnd = new Random();
		
		return this.asMap().values().asList().get(rnd.nextInt(this.underlying.size()));
	}
	
	@Override
	public ImmutableBiMap<ResourceLocation, UniversalPlayerType> asMap() {
		if(!(this.underlying instanceof ImmutableBiMap))
			this.underlying = ImmutableBiMap.copyOf(this.underlying);
		
		return (ImmutableBiMap<ResourceLocation, UniversalPlayerType>) this.underlying;
	}
}
