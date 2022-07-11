package com.example.examplemod.network;

import java.io.IOException;

import com.example.examplemod.client.ClientNetHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.wolftail.util.tracker.ContentDiff;

public class S2CContentDiff implements Packet<ClientNetHandler> {
	
	private ByteBuf data;
	
	public S2CContentDiff() {}
	
	public S2CContentDiff(ContentDiff cd) {
		this.data = cd.toByteBuf();
	}
	
	@Override
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.data = buf.retainedSlice();
		
		buf.readerIndex(buf.writerIndex());
	}
	
	@Override
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeBytes(this.data);
	}
	
	@Override
	public void processPacket(ClientNetHandler handler) {
		PacketThreadUtil.checkThreadAndEnqueue(this, handler, Minecraft.getMinecraft());
		
		ContentDiff.apply(this.data, handler.universe);
		this.data.release();
	}
}