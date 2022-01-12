package com.example.examplemod;

import java.io.IOException;

import com.example.examplemod.ExampleMod.SNetHandler;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.world.WorldServer;

public class C2SForward implements Packet<SNetHandler> {
	
	@Override
	public void readPacketData(PacketBuffer buf) throws IOException {
		
	}
	
	@Override
	public void writePacketData(PacketBuffer buf) throws IOException {
		
	}
	
	@Override
	public void processPacket(SNetHandler handler) {
		PacketThreadUtil.checkThreadAndEnqueue(this, handler, (WorldServer) handler.pig.world);
		
		handler.pig.motionY = 10;
	}
}
