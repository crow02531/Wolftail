package net.wolftail.api;

import javax.annotation.Nonnull;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.wolftail.api.lifecycle.Sealed;
import net.wolftail.internal.core.ImplUPT;

@Sealed
public interface UniversalPlayerType extends IForgeRegistryEntry<UniversalPlayerType> {
	
	ResourceLocation TYPE_PLAYER_ID = new ResourceLocation("minecraft", "player");
	
	UniversalPlayerType TYPE_PLAYER = create(new Introduction("utype.minecraft.player.name",
			"utype.minecraft.player.desc", "minecraft:textures/utype/player.json"), null, null)
			.setRegistryName(TYPE_PLAYER_ID);
	
	boolean hasRegistered();
	
	@Nonnull
	static UniversalPlayerType create(@Nonnull Introduction intro, IServerHandler sh, IClientHandler ch) {
		return new ImplUPT(intro, sh, ch);
	}
}
