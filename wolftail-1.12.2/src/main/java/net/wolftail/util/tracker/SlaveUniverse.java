package net.wolftail.util.tracker;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.world.DimensionType;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;

@SideWith(section = GameSection.GAME_PLAYING)
public final class SlaveUniverse {
	
	final Map<DimensionType, SlaveWorld> worlds;
	
	public SlaveUniverse() {
		this.worlds = new EnumMap<>(DimensionType.class);
	}
	
	public SlaveWorld world(@Nonnull DimensionType dim) {
		return this.worlds.get(dim);
	}
	
	SlaveWorld getOrCreate(DimensionType d) {
		SlaveWorld w = this.worlds.get(d);
		
		if(w == null) 
			this.worlds.put(d, w = new SlaveWorld(this, d));
		
		return w;
	}
}
