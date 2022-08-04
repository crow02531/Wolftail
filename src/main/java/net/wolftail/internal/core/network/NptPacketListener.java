package net.wolftail.internal.core.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.wolftail.api.INetworkHandler;

public abstract class NptPacketListener implements INetHandler, ITickable {
	
	private INetworkHandler handler;
	
	NptPacketListener() {
	}
	
	public void setNetHandler(INetworkHandler h) {
		this.handler = h;
	}
	
	public INetworkHandler getNetHandler() {
		return this.handler;
	}
	
	@Override
	public void update() {
		if (this.handler != null)
			this.handler.tick();
	}
	
	void handlePayload(ByteBuf buf) {
		this.handler.handle(buf);
		
		if (buf.isReadable())
			throw new RuntimeException(
					"Payload was larger than I expected, found " + buf.readableBytes() + " bytes extra");
	}
	
	static void throw0() {
		throw new RuntimeException("Unexpected packet");
	}
	
	static void check0(String channel) {
		if (!channel.equals("WT|PL"))
			throw0();
	}
	
	public static void cleanFML(NetworkManager m) {
		Channel c = m.channel();
		
		// remove from pipeline
		c.pipeline().remove("fml:packet_handler");
		
		// clean attributes
		c.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(null);
		c.attr(NetworkRegistry.NET_HANDLER).set(null);
		c.attr(NetworkDispatcher.FML_DISPATCHER).set(null);
		NetworkRegistry.INSTANCE.cleanAttributes();
	}
}
