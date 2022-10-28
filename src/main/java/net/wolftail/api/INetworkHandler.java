package net.wolftail.api;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;

@SideWith(section = GameSection.GAME_PLAYING)
public interface INetworkHandler {

	/**
	 * Process a packet. Called by netty thread. You can keep
	 * the ref to {@code buf} as long as you want.
	 * 
	 * @param buf the data of the packet, pooled
	 */
	void handle(@Nonnull ByteBuf buf);
}
