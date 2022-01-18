package com.example.examplemod.network;

import java.io.IOException;

import com.example.examplemod.server.ServerNetHandler;

import net.minecraft.entity.passive.EntityPig;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.world.WorldServer;

public class C2SForward implements Packet<ServerNetHandler> {
	
	@Override
	public void readPacketData(PacketBuffer buf) throws IOException {
		
	}
	
	@Override
	public void writePacketData(PacketBuffer buf) throws IOException {
		
	}
	
	@Override
	public void processPacket(ServerNetHandler handler) {
		EntityPig p = handler.getPlayEntity();
		
		PacketThreadUtil.checkThreadAndEnqueue(this, handler, (WorldServer) p.world);
		
		p.motionY = 10;
	}
}
