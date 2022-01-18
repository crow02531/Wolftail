package com.example.examplemod;

import com.example.examplemod.client.ClientCallback;
import com.example.examplemod.server.ServerCallback;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.wolftail.api.UniversalPlayerType;
import net.wolftail.api.UniversalPlayerTypeRegistry;

@Mod(modid = ExampleMod.MODID, name = ExampleMod.NAME, version = ExampleMod.VERSION)
public class ExampleMod {
	
	public static final String MODID = "examplemod";
	public static final String NAME = "Example Mod";
	public static final String VERSION = "1.0";
	
	@EventHandler
    public void init(FMLInitializationEvent event) {
		ServerCallback sc = new ServerCallback();
		ClientCallback cc = new ClientCallback();
		
		UniversalPlayerTypeRegistry.INSTANCE.register(new ResourceLocation("examplemod", "pigs"), UniversalPlayerType.create(sc, cc, cc));
	}
}
