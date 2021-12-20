package net.wolftail.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import net.minecraft.util.ResourceLocation;
import net.wolftail.api.UniversalPlayerType;
import net.wolftail.api.UniversalPlayerTypeRegistry;
import net.wolftail.api.lifecycle.BuiltInSection;
import net.wolftail.api.lifecycle.SectionState;

public final class ImplUPTR implements UniversalPlayerTypeRegistry {
	
	private final BiMap<ResourceLocation, UniversalPlayerType> underlying;
	
	private Set<UniversalPlayerType>					view_set;
	private Map<ResourceLocation, UniversalPlayerType>	view_map;
	
	public ImplUPTR() {
		this.underlying = HashBiMap.create();
	}
	
	@Override
	public UniversalPlayerType register(ResourceLocation id, UniversalPlayerType type) {
		BuiltInSection.GAME_LOADING.ensure(SectionState.ACTIVE, () -> {
			Objects.requireNonNull(id, "id");
			Objects.requireNonNull(type, "type");
			
			if(this.underlying.putIfAbsent(id, type) != null)
				throw new IllegalStateException("The ID " + id + " had been registered");
		});
		
		return type;
	}
	
	@Override
	public UniversalPlayerType registeredAt(ResourceLocation id) {
		return this.underlying.get(id);
	}
	
	@Override
	public ResourceLocation idFor(UniversalPlayerType type) {
		return this.underlying.inverse().get(type);
	}
	
	@Override
	public Map<ResourceLocation, UniversalPlayerType> asMap() {
		Map<ResourceLocation, UniversalPlayerType> result = this.view_map;
		
		return result != null ? result : (this.view_map = new RestrictedViewMap());
	}
	
	@Override
	public Set<UniversalPlayerType> asSet() {
		Set<UniversalPlayerType> result = this.view_set;
		
		return result != null ? result : (this.view_set = Collections.unmodifiableSet(this.underlying.values()));
	}
	
	private class RestrictedViewMap extends ForwardingMap<ResourceLocation, UniversalPlayerType> {
		
		private Set<ResourceLocation>								key_set;
		private Set<Entry<ResourceLocation, UniversalPlayerType>>	entry_set;
		
		@Override
		protected Map<ResourceLocation, UniversalPlayerType> delegate() {
			return ImplUPTR.this.underlying;
		}
		
		@Override
		public UniversalPlayerType remove(Object object) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public UniversalPlayerType put(ResourceLocation key, UniversalPlayerType value) {
			ImplUPTR.this.register(key, value);
			
			return null;
		}
		
		@Override
		public void putAll(Map<? extends ResourceLocation, ? extends UniversalPlayerType> map) {
			map.forEach(ImplUPTR.this::register);
		}
		
		@Override
		public Set<ResourceLocation> keySet() {
			Set<ResourceLocation> result = this.key_set;
			
			return result != null ? result : (this.key_set = Collections.unmodifiableSet(ImplUPTR.this.underlying.keySet()));
		}
		
		@Override
		public Collection<UniversalPlayerType> values() {
			return ImplUPTR.this.asSet();
		}
		
		@Override
		public Set<Entry<ResourceLocation, UniversalPlayerType>> entrySet() {
			Set<Entry<ResourceLocation, UniversalPlayerType>> result = this.entry_set;
			
			return result != null ? result : (this.entry_set = new StandardEntrySet() {
				
				@Override
				public Iterator<Entry<ResourceLocation, UniversalPlayerType>> iterator() {
					return new Itr();
				}
			});
		}
	}
	
	private class Itr implements Iterator<Entry<ResourceLocation, UniversalPlayerType>> {
		
		private final Iterator<Entry<ResourceLocation, UniversalPlayerType>> delegate = ImplUPTR.this.underlying.entrySet().iterator();
		
		@Override
		public Entry<ResourceLocation, UniversalPlayerType> next() {
			Entry<ResourceLocation, UniversalPlayerType> entry = this.delegate.next();
			
			return Maps.immutableEntry(entry.getKey(), entry.getValue());
		}
		
		@Override
		public boolean hasNext() {
			return this.delegate.hasNext();
		}
	}
}
