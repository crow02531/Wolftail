package net.wolftail.api.lifecycle;

import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;
import net.wolftail.impl.SharedImpls;

public enum LogicType {
	
	LOGIC_CLIENT {
		
		@Override
		public boolean inMain() {
			return PhysicalType.INTEGRATED_CLIENT.is() ? Thread.currentThread().getId() == 1 : false;
		}
	},
	
	LOGIC_SERVER {
		
		@Override
		public boolean inMain() {
			if(PhysicalType.INTEGRATED_CLIENT.is()) {
				if(BuiltInSection.GAME_PREPARED.state != SectionState.ACTIVE)
					return false;
				
				IntegratedServer server = Minecraft.getMinecraft().getIntegratedServer();
				
				return server == null ? false : server.isCallingFromMinecraftThread();
			} else {
				Thread regular_host = SharedImpls.Holder0.regular_dedicated_server_host;
				Thread current = Thread.currentThread();
				
				if(current == regular_host) return true;
				if(regular_host == null && current.getId() == 1) return true;
				
				return false;
			}
		}
	};
	
	public abstract boolean inMain();
	
	public void ensureMain() {
		if(!this.inMain())
			throw new IllegalStateException("Not in " + this + "'s main thread");
	}
	
	public static boolean inHost() {
		return PhysicalType.INTEGRATED_CLIENT.is() ? LOGIC_CLIENT.inMain() : LOGIC_SERVER.inMain();
	}
	
	public static void ensureHost() {
		if(!inHost())
			throw new IllegalStateException("Not in " + PhysicalType.currentType() + "'s host thread");
	}
}
