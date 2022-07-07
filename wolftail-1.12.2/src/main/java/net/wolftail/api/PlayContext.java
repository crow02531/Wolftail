package net.wolftail.api;

import java.util.UUID;

import javax.annotation.Nonnull;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;

@SideWith(section = GameSection.GAME_PLAYING, thread = { LogicType.LOGIC_CLIENT, LogicType.LOGIC_SERVER })
public interface PlayContext {
	
	@Nonnull UniversalPlayerType playType();
	
	@Nonnull UUID playId();
	
	@Nonnull String playName();
	
	void sendPacket(@Nonnull Packet<?> packetIn);
	
	void sendPacket(@Nonnull Packet<?> packetIn, GenericFutureListener<? extends Future<? super Void>> listener);
	
	void disconnect();
	
	void setNetHandler(INetHandler handler);
}
