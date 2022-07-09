package net.wolftail.util.tracker;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;

@SideWith(section = GameSection.GAME_PLAYING)
public interface ContentDiff {
	
	@Nonnull SubscribeOrder order();
	
	@Nonnull ByteBuf asByteBuf();
}
