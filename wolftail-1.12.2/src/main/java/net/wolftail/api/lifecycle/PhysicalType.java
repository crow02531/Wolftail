package net.wolftail.api.lifecycle;

import net.minecraft.launchwrapper.Launch;

public enum PhysicalType {
	
	INTEGRATED_CLIENT,
	DEDICATED_SERVER;
	
	public boolean is() {
		return CURRENT_TYPE == this;
	}
	
	public void ensure() {
		if(CURRENT_TYPE != this)
			throw new IllegalStateException("Not in " + this);
	}
	
	public static PhysicalType currentType() {
		return CURRENT_TYPE;
	}
	
	private static final PhysicalType CURRENT_TYPE;
	
	static {
		String force = System.getenv("wolftail.forced_physical_type");
		PhysicalType result;
		
		if(force != null) {
			switch(force) {
			case "client":
				result = INTEGRATED_CLIENT;
				
				break;
			case "server":
				result = DEDICATED_SERVER;
				
				break;
			default:
				throw new IllegalArgumentException("System property 'wolftail.forced_physical_type' has unknown value: " + force);
			}
		} else result = Launch.classLoader.findResource("net/minecraft/client/main/Main.class") == null ? DEDICATED_SERVER : INTEGRATED_CLIENT;
		
		CURRENT_TYPE = result;
	}
}
