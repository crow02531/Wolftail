package net.wolftail.api;

import javax.annotation.Nonnull;

import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;

@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_CLIENT)
public interface IClientHandler {
	
	/**
	 * Called directly by wolftail when game section changes from WANDERING to PLAYING.
	 * 
	 * @param context the playing play context
	 */
	void handleEnter(@Nonnull PlayContext context);
	
	/**
	 * Called directly by wolftail every game loop.
	 */
	void handleFrame();

	/**
	 * Called directly by wolftail every client tick.
	 */
	void handleTick();
	
	/**
	 * Called directly by wolftail when receiving a chat from server.
	 * 
	 * @param type the type of the chat
	 * @param text the content of the chat
	 */
	void handleChat(@Nonnull ChatType type, @Nonnull ITextComponent text);
	
	/**
	 * Called directly by wolftail when game section changes from PLAYING to WANDERING.
	 */
	void handleLeave();
}
