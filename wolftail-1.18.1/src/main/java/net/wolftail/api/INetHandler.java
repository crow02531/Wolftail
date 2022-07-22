package net.wolftail.api;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;

@SideWith(section = GameSection.GAME_PLAYING)
public interface INetHandler {
	
	/**
	 * Process a packet. Called by netty thread. You should read all
	 * readable bytes in {@code buf} before the method returns.
	 * 
	 * <p>
	 * Notice that {@code buf} is pooled so it's not recommended to keep
	 * its ref too long.
	 * </p>
	 * 
	 * @param buf	the whole data of the packet
	 */
	void handle(@Nonnull ByteBuf buf);
	
	/**
	 * Called when the connection is dead.
	 */
	@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_HOST)
	void onDisconnect();
	
	/**
	 * Called every tick.
	 */
	@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_HOST)
	default void tick() {}
}
