package net.wolftail.api.lifecycle;

import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;
import net.wolftail.impl.SharedImpls;

/**
 * There are many threads running in Minecraft. In
 * {@link PhysicalType#INTEGRATED_CLIENT INTEGRATED_CLIENT}, there is a thread
 * responsible of registering game content, doing game loop. This thread is
 * {@link #LOGIC_CLIENT}. When you are in singleplayer, the thread running
 * {@link IntegratedServer} is called {@link #LOGIC_SERVER}. And in
 * {@link PhysicalType#DEDICATED_SERVER DEDICATED_SERVER}, there isn't logic client
 * , the thread that registers game content and ticking the server is logic server.
 * 
 * <p>
 * If a thread is found logic client(or logic server), then the thread is logic
 * client(or logic server) till its death. There is only one logic server thread at
 * a time. However in two distinct time, the logic server thread could be different.
 * Think about leaving a singleplayer and start a new singleplayer. And in dedicated
 * server the thread registering game content and the thread ticking the server is
 * different. See {@link net.minecraft.server.MinecraftServer#main(String[])}.
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
			if(PhysicalType.INTEGRATED_CLIENT.is()) {
				if(SharedImpls.H1.TOKEN_PREPARED.state != SectionState.ACTIVE)
					return false;
				
				IntegratedServer server = Minecraft.getMinecraft().getIntegratedServer();
				
				return server == null ? false : server.isCallingFromMinecraftThread();
			} else {
				Thread regular_host = SharedImpls.H0.regular_dedicated_server_host;
				Thread current = Thread.currentThread();
				
				if(current == regular_host) return true;
				if(regular_host == null && current.getId() == 1) return true;
				
				return false;
			}
		}
	};
	
	public abstract boolean in();
	
	public void ensure() {
		if(!this.in())
			throw new IllegalStateException("Not in " + this);
	}
	
	/**
	 * In {@link PhysicalType#INTEGRATED_CLIENT INTEGRATED_CLIENT}, the host thread
	 * is {@link #LOGIC_CLIENT}. And in {@link PhysicalType#DEDICATED_SERVER DEDICATED_SERVER}
	 * , {@link #LOGIC_SERVER}.
	 * 
	 * @return true means the current thread is host thread
	 */
	public static boolean inHost() {
		return PhysicalType.INTEGRATED_CLIENT.is() ? LOGIC_CLIENT.in() : LOGIC_SERVER.in();
	}
	
	public static void ensureHost() {
		if(!inHost())
			throw new IllegalStateException("Not in " + PhysicalType.currentType() + "'s host thread");
	}
}
