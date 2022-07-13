package net.wolftail.util.tracker;

import javax.annotation.Nonnull;

import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;

@SideWith(section = GameSection.GAME_PLAYING)
public final class SlaveTime {
	
	private SlaveWorld world;
	
	long dayTime;
	
	SlaveTime(SlaveWorld w) {
		this.world = w;
	}
	
	@Nonnull
	public SlaveWorld world() {
		return this.check().world;
	}
	
	public void release() {
		this.check().world.time = null;
		
		this.world = null;
	}
	
	public boolean valid() {
		return this.world != null;
	}
	
	private SlaveTime check() {
		if(!this.valid())
			throw new IllegalStateException();
		
		return this;
	}
	
	public long dayTime() {
		return this.check().dayTime;
	}
}
