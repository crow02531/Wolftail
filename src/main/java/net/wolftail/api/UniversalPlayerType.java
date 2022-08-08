package net.wolftail.api;

import javax.annotation.Nonnull;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.wolftail.api.lifecycle.Sealed;
import net.wolftail.internal.core.ImplUPT;

@Sealed
public interface UniversalPlayerType extends IForgeRegistryEntry<UniversalPlayerType> {
	
	ResourceLocation TYPE_PLAYER_ID = new ResourceLocation("wolftail", "player");
	
	UniversalPlayerType TYPE_PLAYER = create(null, null, new Introduction("utype.wolftail.player.name",
			"utype.wolftail.player.desc", "wolftail:textures/utype/player.json")).setRegistryName(TYPE_PLAYER_ID);
	
	boolean hasRegistered();
	
	@Nonnull
	static UniversalPlayerType create(IServerHandler sh, IClientHandler ch, @Nonnull Introduction intro) {
		return new ImplUPT(sh, ch, intro);
	}
}
