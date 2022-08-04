package net.wolftail.internal.core;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.wolftail.api.UniversalPlayerType;
import net.wolftail.api.lifecycle.Wolftail;

// add new registry
@EventBusSubscriber(modid = Wolftail.MOD_ID)
public class RegistryHolder {
	
	public static final ResourceLocation REGISTRY_ID = new ResourceLocation(Wolftail.MOD_ID, "uniplayertypes");
	
	public static IForgeRegistry<UniversalPlayerType> getRegistry() {
		return registry;
	}
	
	private static IForgeRegistry<UniversalPlayerType> registry;
	
	@SubscribeEvent
	public static void event_newRegistry(RegistryEvent.NewRegistry e) {
		registry = new RegistryBuilder<UniversalPlayerType>().setType(UniversalPlayerType.class).setName(REGISTRY_ID)
				.create();
		
		registry.register(UniversalPlayerType.TYPE_PLAYER);
	}
}
