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

/**
 * Play context will be setup the moment the vanilla connection between client and server
 * upgrades to a wolftail connection. The server side will have a {@link ServerPlayContext}
 * while the client side, a {@link ClientPlayContext}.
 * 
 * @see ClientPlayContext
 * @see ServerPlayContext
 */
@SideWith(section = GameSection.GAME_PLAYING, thread = { LogicType.LOGIC_CLIENT, LogicType.LOGIC_SERVER })
public interface PlayContext {
	
	@Nonnull UniversalPlayerType playType();
	
	@Nonnull UUID playId();
	
	@Nonnull String playName();
	
	void sendPacket(@Nonnull Packet<?> packetIn);
	
	void sendPacket(@Nonnull Packet<?> packetIn, GenericFutureListener<? extends Future<? super Void>> listener);
	
	void disconnect();
	
	/**
	 * Set the net handler of context's connection. The handler
	 * will be used in packet receiving and disconnect handling.
	 * If the net handler implements {@link net.minecraft.util.ITickable ITickable}
	 * , it will get call every tick.
	 * 
	 * @param handler	the net handler, can't be null
	 */
	void setNetHandler(@Nonnull INetHandler handler);
}
