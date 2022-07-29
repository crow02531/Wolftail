package com.example.examplemod;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.wolftail.api.UniversalPlayerTypeRegistry;

@Mod(modid = ExampleMod.MODID, name = ExampleMod.NAME, version = ExampleMod.VERSION)
public class ExampleMod {
	
	public static final String MODID = "examplemod";
	public static final String NAME = "Example Mod";
	public static final String VERSION = "1.0";
	
	public static final ResourceLocation TYPE_PIG_ID = new ResourceLocation("examplemod", "pig");
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		UniversalPlayerTypeRegistry.INSTANCE.register(TYPE_PIG_ID, PigServerHandler.INSTANCE, PigClientHandler.INSTANCE);
	}
}
