package net.wolftail.api.lifecycle;

import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;
import net.wolftail.impl.SharedImpls;

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
				if(SharedImpls.H1.token_prepared.currentState() != SectionState.ACTIVE)
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
	
	public static boolean inHost() {
		return PhysicalType.INTEGRATED_CLIENT.is() ? LOGIC_CLIENT.in() : LOGIC_SERVER.in();
	}
	
	public static void ensureHost() {
		if(!inHost())
			throw new IllegalStateException("Not in " + PhysicalType.currentType() + "'s host thread");
	}
}
