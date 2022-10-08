package net.wolftail.api;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;

@SideWith(section = GameSection.GAME_PLAYING)
public interface INetworkHandler {

	/**
	 * Process a packet. Called by netty thread. An exception will be thrown if
	 * there are readable bytes after this method returns.
	 * 
	 * @param buf the data of the packet, pooled
	 */
	void handle(@Nonnull ByteBuf buf);
}
