package net.wolftail.api.lifecycle;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.wolftail.internal.core.SectionHandler;
import net.wolftail.util.MoreServers;

/**
 * There are many threads running in Minecraft. In
 * {@link PhysicalType#INTEGRATED_CLIENT INTEGRATED_CLIENT}, there is a thread
 * responsible of registering game data(items, blocks, etc.), doing game loop.
 * This thread is {@link #LOGIC_CLIENT}. When you are in singleplayer, the
 * thread running {@link IntegratedServer} is called {@link #LOGIC_SERVER}. And
 * in {@link PhysicalType#DEDICATED_SERVER DEDICATED_SERVER}, there isn't logic
 * client , the thread that registers game data and ticking the server is logic
 * server.
 * 
 * <p>
 * There is only one logic server thread at a time. However in two distinct
 * time, the logic server thread could be different. Think about in singleplayer
 * you leave a world and rejoin it. In dedicated server the thread registering
 * game data and the thread ticking the server are different. See
 * {@link MinecraftServer#main(String[])}. The job of 'logic server' transfers
 * from the old thread to a new one in
 * {@link MinecraftServer#startServerThread()}.
 * </p>
 * 
 * <p>
 * Contrary to logic server, there's only one logic client.
 * </p>
 * 
 * @see PhysicalType
 */
public enum LogicType {
	
	LOGIC_CLIENT {
		
		@Override
		public boolean in() {
			return PhysicalType.INTEGRATED_CLIENT.is() ? Thread.currentThread().getId() == 1 : false;
		}
	},
	
	LOGIC_SERVER {
		
		@Override
		public boolean in() {
			if (PhysicalType.INTEGRATED_CLIENT.is()) {
				if (SectionHandler.HANDLER_PREPARED.getState() != SectionState.ACTIVE)
					return false;
				
				MinecraftServer server = MoreServers.serverInstance();
				
				return server == null ? false : server.isCallingFromMinecraftThread();
			} else {
				Thread regular = SectionHandler.dedicatedServerRegularThread;
				Thread current = Thread.currentThread();
				
				if (current == regular)
					return true;
				if (regular == null && current.getId() == 1)
					return true;
				
				return false;
			}
		}
	},
	
	/**
	 * In client, logic host thread is the logic client thread, and in dedicated
	 * server, the logic server thread.
	 */
	LOGIC_HOST {
		
		@Override
		public boolean in() {
			return PhysicalType.INTEGRATED_CLIENT.is() ? LOGIC_CLIENT.in() : LOGIC_SERVER.in();
		}
	};
	
	/**
	 * @return true if the logic type of current thread is {@code this}
	 */
	public abstract boolean in();
	
	/**
	 * Ensure {@code in()} return true.
	 * 
	 * @throws IllegalStateException when the current thread is not the desiring one
	 * 
	 * @see #in()
	 */
	public void ensure() {
		if (!this.in())
			throw new IllegalStateException("Not in " + this);
	}
}
