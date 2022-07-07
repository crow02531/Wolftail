package net.wolftail.impl.network;

import java.io.IOException;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

public final class BiDPacketNPTKeepAlive implements Packet<INetHandler> {
	
	@Override
	public void readPacketData(PacketBuffer buf) throws IOException {}
	
	@Override
	public void writePacketData(PacketBuffer buf) throws IOException {}
	
	@Override
	public void processPacket(INetHandler handler) {
		//NOOP we are not handled this way
	}
}
