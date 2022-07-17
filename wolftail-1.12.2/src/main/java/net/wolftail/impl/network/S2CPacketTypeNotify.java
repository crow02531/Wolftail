package net.wolftail.impl.network;

import java.io.IOException;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.network.login.server.SPacketLoginSuccess;
import net.minecraft.util.ResourceLocation;
import net.wolftail.api.UniversalPlayerType;
import net.wolftail.api.UniversalPlayerTypeRegistry;
import net.wolftail.impl.ExtensionsNetHandlerLoginClient;
import net.wolftail.impl.ImplUPT;
import net.wolftail.impl.SharedImpls;

public final class S2CPacketTypeNotify implements Packet<INetHandlerLoginClient> {
	
	private ResourceLocation type_id;
	
	public S2CPacketTypeNotify() {}
	
	public S2CPacketTypeNotify(ImplUPT type) {
		this.type_id = type.registeringId();
	}
	
	@Override
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.type_id = buf.readResourceLocation();
	}
	
	@Override
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeResourceLocation(this.type_id);
	}
	
	@Override
	public void processPacket(INetHandlerLoginClient handler) {
		ImplUPT type = (ImplUPT) UniversalPlayerTypeRegistry.INSTANCE.registeredAt(this.type_id);
		
		if(type == null)
			throw new IllegalStateException("Unknow universal player type " + this.type_id);
		
		ExtensionsNetHandlerLoginClient handler_ext = SharedImpls.as(handler);
		SPacketLoginSuccess packet = handler_ext.wolftail_getStoredLoginSuccessPacket();
		
		//we now should in the netty's thread
		SharedImpls.get_mc_as().wolftail_loginSuccess(type, packet.getProfile().getId(), handler_ext.wolftail_getConnection());
		
		if(type == UniversalPlayerType.TYPE_PLAYER)
			handler.handleLoginSuccess(packet);
		
		handler_ext.wolftail_clearStoredPacketRef();
	}
}
