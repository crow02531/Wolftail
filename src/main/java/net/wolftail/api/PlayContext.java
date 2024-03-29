package net.wolftail.api;

import java.util.UUID;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.util.text.ITextComponent;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.Sealed;
import net.wolftail.api.lifecycle.SideWith;

@Sealed
@SideWith(section = GameSection.GAME_PLAYING)
public interface PlayContext {
	
	@Nonnull
	EnumPacketDirection side();
	
	@Nonnull
	UniversalPlayerType playType();
	
	@Nonnull
	UUID playId();
	
	@Nonnull
	String playName();
	
	/**
	 * Return the byte buf allocator for the play context.
	 * 
	 * @return the alloc for the play context
	 */
	@Nonnull
	ByteBufAllocator alloc();
	
	/**
	 * Identical to {@code sendPacket(buf, null)}.
	 * 
	 * @see #send(ByteBuf, GenericFutureListener)
	 */
	default void send(@Nonnull ByteBuf buf) {
		this.send(buf, (GenericFutureListener<? extends Future<? super Void>>) null);
	}
	
	/**
	 * Similar to {@link #send(ByteBuf, GenericFutureListener)}, except this method
	 * ignore the future.
	 * 
	 * @see #send(ByteBuf, GenericFutureListener)
	 */
	default void send(@Nonnull ByteBuf buf, Runnable listener) {
		if (listener == null)
			this.send(buf);
		else
			this.send(buf, f -> listener.run());
	}
	
	/**
	 * Plan to send a packet. Notice that the packet may still in outbound pipeline
	 * after this method returns. We will call {@code buf.release()} once the work
	 * has done.
	 * 
	 * @param buf      the payload of the packet, callers are responsible of making
	 *                 all its readable bytes unchanged before the packet actually
	 *                 sent.
	 * @param listener called when the packet had been sent
	 * 
	 * @throws UnsupportedOperationException if this is a
	 *                                       {@link UniversalPlayerType#TYPE_PLAYER
	 *                                       TYPE_PLAYER}
	 */
	void send(@Nonnull ByteBuf buf, GenericFutureListener<? extends Future<? super Void>> listener);
	
	/**
	 * Set the network handler of the connection.
	 * 
	 * <p>
	 * If it was a server side play context, then the only thread allowed to invoke
	 * this method is logic server; The same applies to client side play context.
	 * </p>
	 * 
	 * @param handler the network handler
	 * 
	 * @throws UnsupportedOperationException if this is a
	 *                                       {@link UniversalPlayerType#TYPE_PLAYER
	 *                                       TYPE_PLAYER}
	 */
	void setHandler(INetworkHandler handler);
	
	/**
	 * Get the network handler of the connection.
	 * 
	 * <p>
	 * If it was a server side play context, then the only thread allowed to invoke
	 * this method is logic server; The same applies to client side play context.
	 * </p>
	 * 
	 * @return current network handler
	 * 
	 * @throws UnsupportedOperationException if this is a
	 *                                       {@link UniversalPlayerType#TYPE_PLAYER
	 *                                       TYPE_PLAYER}
	 */
	INetworkHandler getHandler();
	
	/**
	 * Identical to {@code disconnect(null)}. Disconnect normally, such as quitting
	 * game.
	 * 
	 * @see #disconnect(ITextComponent)
	 */
	default void disconnect() {
		this.disconnect(null);
	}
	
	/**
	 * Disconnect and wait until it actually disconnects. Have no effects if the
	 * connection has already disconnected.
	 * 
	 * @param reason the disconnected reason, null indicates disconnecting normally
	 */
	void disconnect(ITextComponent reason);
	
	boolean isConnected();
}
