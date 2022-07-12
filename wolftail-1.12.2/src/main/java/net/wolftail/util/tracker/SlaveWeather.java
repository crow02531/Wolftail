package net.wolftail.util.tracker;

import javax.annotation.Nonnull;

import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;

@SideWith(section = GameSection.GAME_PLAYING)
public final class SlaveWeather {
	
	private SlaveWorld world;
	
	float rainingStrength;
	float thunderingStrength;
	
	SlaveWeather(SlaveWorld w) {
		this.world = w;
	}
	
	@Nonnull
	public SlaveWorld world() {
		return this.check().world;
	}
	
	public void release() {
		this.check().world.weather = null;
		
		this.world = null;
	}
	
	public boolean valid() {
		return this.world != null;
	}
	
	private SlaveWeather check() {
		if(!this.valid())
			throw new IllegalStateException();
		
		return this;
	}
	
	public float rainingStrength() {
		return this.check().rainingStrength;
	}
	
	public float thunderingStrength() {
		return this.check().thunderingStrength;
	}
}
