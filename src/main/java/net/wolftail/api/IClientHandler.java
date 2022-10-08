package net.wolftail.api;

import javax.annotation.Nonnull;

import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;

/**
 * All methods defined here can only be called directly by Wolftail system.
 */
@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_CLIENT)
public interface IClientHandler {
	
	/**
	 * Called when game section changes from WANDERING to PLAYING.
	 * 
	 * @param context the playing play context
	 */
	void handleEnter(@Nonnull PlayContext context);
	
	/**
	 * Called every game loop.
	 */
	void handleFrame();

	/**
	 * Called every client tick.
	 */
	void handleTick();
	
	/**
	 * Called when receiving a chat from server.
	 * 
	 * @param type the type of the chat
	 * @param text the content of the chat
	 */
	void handleChat(@Nonnull ChatType type, @Nonnull ITextComponent text);
	
	/**
	 * Called when game section changes from PLAYING to WANDERING.
	 */
	void handleLeave();
}
