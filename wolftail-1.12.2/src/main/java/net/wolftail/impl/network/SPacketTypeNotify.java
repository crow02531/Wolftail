package net.wolftail.impl.network;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.network.login.server.SPacketLoginSuccess;
import net.minecraft.util.ResourceLocation;
import net.wolftail.api.UniversalPlayerType;
import net.wolftail.api.UniversalPlayerTypeRegistry;
import net.wolftail.impl.ExtensionsNetHandlerLoginClient;
import net.wolftail.impl.ImplPCClient;
import net.wolftail.impl.ImplUPT;
import net.wolftail.impl.SharedImpls;

public final class SPacketTypeNotify implements Packet<INetHandlerLoginClient> {
	
	private ResourceLocation type_id;
	
	public SPacketTypeNotify() {}
	
	public SPacketTypeNotify(ImplUPT type) {
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
		NetworkManager connect = handler_ext.wolftail_getConnection();
		SPacketLoginSuccess packet = handler_ext.wolftail_getStoredLoginSuccessPacket();
		
		//we now should in the netty's thread
		awaitSilently(Minecraft.getMinecraft().addScheduledTask(() -> {
			ImplPCClient context = SharedImpls.get_mc_as().wolftail_setupPlayContext(type, packet.getProfile().getId(), connect);
			
			SharedImpls.as(connect).wolftail_setPlayContext(context);
			
			SharedImpls.LOGGER_NETWORK.info("Client side Wolftail connection set up, with remote address {}", connect.getRemoteAddress());
			SharedImpls.LOGGER_USER.info("The universal player type in use is {}", this.type_id);
			
			if(type != UniversalPlayerType.TYPE_PLAYERS) {
				handler_ext.wolftail_npt_clearStoredPacketRef();
				
				connect.setNetHandler(new DefaultNetHandler());
			}
		}));
		
		if(type == UniversalPlayerType.TYPE_PLAYERS)
			handler.handleLoginSuccess(packet);
	}
	
	private static void awaitSilently(Future<?> f) {
		try {
			f.get();
		} catch(InterruptedException | ExecutionException e) {
			//NOOP
		}
	}
}
