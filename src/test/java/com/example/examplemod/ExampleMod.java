package com.example.examplemod;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wolftail.api.UniversalPlayerType;

@EventBusSubscriber
@Mod(modid = ExampleMod.MODID, name = ExampleMod.NAME, version = ExampleMod.VERSION)
public class ExampleMod {
	
	public static final String MODID = "examplemod";
	public static final String NAME = "Example Mod";
	public static final String VERSION = "1.0";
	
	public static final ResourceLocation TYPE_PIG_ID = new ResourceLocation("examplemod", "pig");
	public static final UniversalPlayerType TYPE_PIG = UniversalPlayerType.create(PigServerHandler.INSTANCE,
			PigClientHandler.INSTANCE).setRegistryName(TYPE_PIG_ID);
	
	@SubscribeEvent
	public static void event_registerUniplayerTypes(RegistryEvent.Register<UniversalPlayerType> e) {
		e.getRegistry().register(TYPE_PIG);
	}
}
