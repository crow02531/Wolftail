package net.wolftail.util;

import java.io.File;

import javax.annotation.Nonnull;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;

public final class MoreServers {
	
	private MoreServers() {
	}
	
	/**
	 * Get the current minecraft server instance. In dedicated server this method
	 * always returns the same value, but in integrated client it returns the
	 * current integrated server.
	 * 
	 * @return the current minecraft server instance
	 */
	@Nonnull
	@SideWith(section = GameSection.GAME_PLAYING)
	public static MinecraftServer serverInstance() {
		return FMLCommonHandler.instance().getMinecraftServerInstance();
	}
	
	/**
	 * Get the world directory of the given server.
	 * 
	 * @param server the server
	 * 
	 * @return the directory
	 */
	@Nonnull
	public static File dirOf(@Nonnull MinecraftServer server) {
		return server.worlds[0].getSaveHandler().getWorldDirectory();
	}
}
