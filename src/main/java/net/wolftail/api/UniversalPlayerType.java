package net.wolftail.api;

import javax.annotation.Nonnull;

import net.minecraft.util.ResourceLocation;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.impl.core.ImplUPT;

/**
 * Represent an uniplayer type.
 */
public interface UniversalPlayerType {
	
	ResourceLocation	TYPE_PLAYER_ID	= new ResourceLocation("minecraft", "player");
	UniversalPlayerType	TYPE_PLAYER		= new ImplUPT();
	
	@Nonnull
	@SideWith(section = GameSection.GAME_PREPARED)
	default ResourceLocation registeringId() {
		return UniversalPlayerTypeRegistry.INSTANCE.idFor(this);
	}
	
	default boolean isPlayerType() {
		return this == TYPE_PLAYER;
	}
}
