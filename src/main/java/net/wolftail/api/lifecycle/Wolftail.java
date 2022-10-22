package net.wolftail.api.lifecycle;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.InstanceFactory;

@Mod(modid = Wolftail.MOD_ID, version = Wolftail.MOD_VERSION, acceptedMinecraftVersions = Wolftail.MC_VERSION)
public final class Wolftail {
	
	public static final String MC_VERSION = "1.12.2";
	
	public static final String MOD_ID = "wolftail";
	public static final String MOD_VERSION = "0.4.0";
	
	public static final Wolftail MOD_INSTANCE = new Wolftail();
	
	private Wolftail() {
	}
	
	@InstanceFactory
	private static Object fml_getModInstance() {
		// manually set display version
		Loader.instance().activeModContainer().getMetadata().version = MOD_VERSION;
		
		return MOD_INSTANCE;
	}
}
