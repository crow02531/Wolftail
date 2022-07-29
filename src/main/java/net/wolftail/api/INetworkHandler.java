package net.wolftail.api;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;

@SideWith(section = GameSection.GAME_PLAYING)
public interface INetworkHandler {
	
	/**
	 * Process a packet. Called by netty thread. You should read all readable bytes
	 * in {@code buf} before the method returns.
	 * 
	 * <p>
	 * Notice that {@code buf} is pooled so it's not recommended to keep its ref too
	 * long.
	 * </p>
	 * 
	 * @param buf the data of the packet
	 */
	void handle(@Nonnull ByteBuf buf);
	
	/**
	 * Called every (network) tick.
	 */
	@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_HOST)
	default void tick() {
	}
}
