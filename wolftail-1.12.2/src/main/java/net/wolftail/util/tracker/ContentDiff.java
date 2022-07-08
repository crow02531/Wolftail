package net.wolftail.util.tracker;

import javax.annotation.Nonnull;

import net.minecraft.network.PacketBuffer;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;

@SideWith(section = GameSection.GAME_PLAYING)
public interface ContentDiff {
	
	@Nonnull SubscribeOrder order();
	
	void writeTo(@Nonnull PacketBuffer buf);
	
	@Nonnull
	public static ContentDiff readFrom(@Nonnull PacketBuffer buf) {
		return null;
	}
}
