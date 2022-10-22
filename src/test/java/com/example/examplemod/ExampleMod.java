package com.example.examplemod;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wolftail.api.Introduction;
import net.wolftail.api.UniversalPlayerType;
import net.wolftail.api.lifecycle.PhysicalType;

//TODO a thorough example
@EventBusSubscriber
@Mod(modid = ExampleMod.MODID, name = ExampleMod.NAME, version = ExampleMod.VERSION)
public class ExampleMod {
	
	public static final String MODID = "examplemod";
	public static final String NAME = "Example Mod";
	public static final String VERSION = "1.0";
	
	public static final ResourceLocation TYPE_PIG_ID = new ResourceLocation("examplemod", "pig");
	public static final UniversalPlayerType TYPE_PIG = UniversalPlayerType
			.create(PigServerHandler.INSTANCE, PhysicalType.INTEGRATED_CLIENT.is() ? PigClientHandler.INSTANCE : null,
					new Introduction("utype.examplemod.pig.name", "utype.examplemod.pig.desc"))
			.setRegistryName(TYPE_PIG_ID);
	
	@SubscribeEvent
	public static void event_registerUniplayerTypes(RegistryEvent.Register<UniversalPlayerType> e) {
		e.getRegistry().register(TYPE_PIG);
	}
}
