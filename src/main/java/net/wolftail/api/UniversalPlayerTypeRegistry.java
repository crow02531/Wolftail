package net.wolftail.api;

import java.util.Random;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableBiMap;

import net.minecraft.util.ResourceLocation;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.impl.core.ImplUPTR;

/**
 * The registry of uniplayer type. One should register only during
 * the {@link GameSection#GAME_LOADING LOADING} section. The typical
 * use of Wolftail is:
 * 
 * <pre>
 *   //during loading section
 *   ResourceLocation myType_id = new ResourceLocation("modid", "someName");
 *   UniversalPlayerType myType;
 *   
 *   myType = UniversalPlayerTypeRegistry.INSTANCE.register(myType_id, myCallback0, myCallback1, myCallback2);
 * </pre>
 * 
 * That's all you should do. After that when entering the game, if {@code myType}
 * is chosen as the entering player's type, {@code myCallback*} would get called.
 * 
 * @see UniversalPlayerType
 * @see #register(ResourceLocation, IServerEntryPoint, IClientEntryPoint, IClientFrameCallback)
 */
public interface UniversalPlayerTypeRegistry {
	
	UniversalPlayerTypeRegistry INSTANCE = new ImplUPTR();
	
	/**
	 * The ONLY way to get an instance of {@link UniversalPlayerType}.
	 * 
	 * @param id	the id of your uniplayer type
	 * 
	 * @param entry_point_server	the server entry point
	 * @param entry_point_client	the client entry point
	 * @param callback_frame		the client frame callback
	 * 
	 * @return a newly created registered uniplayer type
	 * 
	 * @see IServerEntryPoint
	 * @see IClientEntryPoint
	 * @see IClientFrameCallback
	 */
	@SideWith(section = GameSection.GAME_LOADING, thread = { LogicType.LOGIC_CLIENT, LogicType.LOGIC_SERVER })
	@Nonnull UniversalPlayerType register(@Nonnull ResourceLocation id, IServerEntryPoint entry_point_server, IClientEntryPoint entry_point_client, IClientFrameCallback callback_frame);
	
	@SideWith(section = GameSection.GAME_PREPARED)
	ResourceLocation idFor(UniversalPlayerType type);
	
	@SideWith(section = GameSection.GAME_LOADED)
	UniversalPlayerType registeredAt(ResourceLocation id);
	
	@SideWith(section = GameSection.GAME_LOADED)
	@Nonnull UniversalPlayerType getRandomType(Random rnd);
	
	@SideWith(section = GameSection.GAME_LOADED)
	@Nonnull ImmutableBiMap<ResourceLocation, UniversalPlayerType> asMap();
}
