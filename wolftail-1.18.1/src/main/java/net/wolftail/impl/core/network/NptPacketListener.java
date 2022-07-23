package net.wolftail.impl.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.resources.ResourceLocation;
import net.wolftail.api.INetHandler;

public sealed abstract class NptPacketListener implements PacketListener
permits NptClientPacketListener, NptServerPacketListener {
	
	private INetHandler handler;
	
	@Override
	public Connection getConnection() {
		return null; //unused
	}
	
	public void setNetHandler(INetHandler h) {
		this.handler = h;
	}
	
	public INetHandler getNetHandler() {
		return this.handler;
	}
	
	public void tick() {
		if(this.handler != null)
			this.handler.tick();
	}
	
	void handlePayload(ByteBuf buf) {
		this.handler.handle(buf);
		
		if(buf.isReadable())
			throw new RuntimeException("Packet was larger than I expected, found " + buf.readableBytes() + " bytes extra");
	}
	
	static void throw0() {
		throw new RuntimeException("Unexpected packet");
	}
	
	static void check0(ResourceLocation channel) {
		if(!Constants.CHANNEL_PLAY_PAYLOAD.equals(channel))
			throw0();
	}
}
