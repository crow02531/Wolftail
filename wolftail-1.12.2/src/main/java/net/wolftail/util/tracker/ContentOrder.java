package net.wolftail.util.tracker;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;

@Immutable
@SideWith(section = GameSection.GAME_PLAYING)
public abstract class ContentOrder {
	
	ContentOrder() {}
	
	@Nonnull
	public abstract ContentType type();
	
	@Override
	public abstract int hashCode();
	
	@Override
	public abstract boolean equals(Object o);
}
