package net.wolftail.api;

import net.minecraft.util.ResourceLocation;
import net.wolftail.impl.ImplUPT;

public interface UniversalPlayerType {
	
	ResourceLocation	TYPE_PLAYERS_ID	= new ResourceLocation("minecraft", "players");
	UniversalPlayerType	TYPE_PLAYERS	= UniversalPlayerTypeRegistry.INSTANCE.register(TYPE_PLAYERS_ID, new ImplUPT());
	
	default boolean registered() {
		return this.registeringId() != null;
	}
	
	default ResourceLocation registeringId() {
		return UniversalPlayerTypeRegistry.INSTANCE.idFor(this);
	}
	
	static UniversalPlayerType create(ServerEntryPoint entry_point_server) {
		return new ImplUPT(entry_point_server, null, null);
	}
	
	static UniversalPlayerType create(ServerEntryPoint entry_point_server, ClientEntryPoint entry_point_client, ClientFrameCallback callback_frame) {
		return new ImplUPT(entry_point_server, entry_point_client, callback_frame);
	}
}
