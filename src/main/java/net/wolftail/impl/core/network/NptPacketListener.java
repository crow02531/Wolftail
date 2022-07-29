package net.wolftail.impl.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;
import net.minecraft.util.ITickable;
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
}
