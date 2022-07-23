package net.wolftail.api;

import java.util.UUID;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.protocol.PacketFlow;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;

@SideWith(section = GameSection.GAME_PLAYING)
public interface PlayContext {
	
	@Nonnull PacketFlow side();
	
	@Nonnull UniversalPlayerType playType();
	
	@Nonnull UUID playId();
	
	@Nonnull String playName();
	
	/**
	 * Identical to {@code sendPacket(buf, null)}.
	 * 
	 * @see #sendPacket(ByteBuf, GenericFutureListener)
	 */
	void sendPacket(@Nonnull ByteBuf buf);
	
	/**
	 * Plan to send a packet. Notice that the packet may still in outbound pipeline
	 * after this method returns. This method will read all readable bytes in {@code buf}.
	 * 
	 * @param buf		the whole data of the packet, callers are responsible of making
	 * 		all its readable bytes unchanged before the packet actually sent.
	 * @param listener	called when the packet had been sent.
	 * 
	 * @throws UnsupportedOperationException	if this is a
	 * 		{@link UniversalPlayerType#TYPE_PLAYER TYPE_PLAYER}
	 */
	void sendPacket(@Nonnull ByteBuf buf, GenericFutureListener<? extends Future<? super Void>> listener);
	
	/**
	 * Set the net handler of the connection.
	 * 
	 * @param handler	the net handler
	 * 
	 * @throws UnsupportedOperationException	if this is a
	 * 		{@link UniversalPlayerType#TYPE_PLAYER TYPE_PLAYER}
	 */
	@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_HOST)
	void setNetHandler(INetHandler handler);
	
	/**
	 * Get the net handler of the connection.
	 * 
	 * @return current net handler
	 * 
	 * @throws UnsupportedOperationException	if this is a
	 * 		{@link UniversalPlayerType#TYPE_PLAYER TYPE_PLAYER}
	 */
	@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_HOST)
	INetHandler getNetHandler();
	
	/**
	 * Disconnect and wait until it actually disconnects. Have no effects if the
	 * connection has already disconnected.
	 */
	void disconnect(); //TODO consider adding disconnect reason
	
	boolean isConnected();
}
