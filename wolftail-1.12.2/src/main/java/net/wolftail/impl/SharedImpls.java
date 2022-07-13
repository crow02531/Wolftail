package net.wolftail.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.wolftail.api.lifecycle.SectionState;
import net.wolftail.util.tracker.ContentDiff;
import net.wolftail.util.tracker.ContentType;
import net.wolftail.util.tracker.OrderChunkNormal;
import net.wolftail.util.tracker.OrderWorldNormal;

public final class SharedImpls {
	
	private SharedImpls() {}
	
	public static final Logger LOGGER_LIFECYCLE	= LogManager.getLogger("Wolftail|Lifecycle");
	public static final Logger LOGGER_NETWORK	= LogManager.getLogger("Wolftail|Network");
	public static final Logger LOGGER_USER		= LogManager.getLogger("Wolftail|User");
	
	public static ExtensionsMinecraftServer as(MinecraftServer arg) {
		return as((Object) arg);
	}
	
	public static ExtensionsNetworkManager as(NetworkManager arg) {
		return as((Object) arg);
	}
	
	public static ExtensionsMinecraft as(Minecraft arg) {
		return as((Object) arg);
	}
	
	public static ExtensionsNetHandlerLoginClient as(INetHandlerLoginClient arg) {
		return as((Object) arg);
	}
	
	public static ExtensionsChunk as(Chunk arg) {
		return as((Object) arg);
	}
	
	public static ExtensionsWorldServer as(WorldServer arg) {
		return as((Object) arg);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T as(Object arg) {
		return (T) arg;
	}
	
	public static ExtensionsMinecraft get_mc_as() {
		return as(Minecraft.getMinecraft());
	}
	
	public static <E> Set<E> wrap(Collection<E> wrapped) {
		return new Set<E>() {
			
			@Override public int size() { return wrapped.size(); }
			@Override public boolean isEmpty() { return wrapped.isEmpty(); }
			@Override public boolean contains(Object o) { return wrapped.contains(o); }
			@Override public Iterator<E> iterator() { return wrapped.iterator(); }
			@Override public Object[] toArray() { return wrapped.toArray(); }
			@Override public <T> T[] toArray(T[] a) { return wrapped.toArray(a); }
			@Override public boolean add(E e) { return wrapped.add(e); }
			@Override public boolean remove(Object o) { return wrapped.remove(o); }
			@Override public boolean containsAll(Collection<?> c) { return wrapped.containsAll(c); }
			@Override public boolean addAll(Collection<? extends E> c) { return wrapped.addAll(c); }
			@Override public boolean retainAll(Collection<?> c) { return wrapped.retainAll(c); }
			@Override public boolean removeAll(Collection<?> c) { return wrapped.removeAll(c); }
			@Override public void clear() { wrapped.clear(); }
		};
	}
	
	public static final class H1 {
		
		public static Thread regular_dedicated_server_host; //dedicatedServer has two host thread, the second one is called regular by us
		
		public final ReentrantReadWriteLock lock;
		
		public SectionState state;
		
		public H1() {
			this(SectionState.BEFORE);
		}
		
		public H1(SectionState init) {
			this.lock = new ReentrantReadWriteLock();
			
			this.state = init;
		}
		
		public void doLock() {
			this.lock.writeLock().lock();
		}
		
		public void doAdvance() {
			this.state = this.state.advance();
		}
		
		public void doUnlock() {
			this.lock.writeLock().unlock();
		}
		
		public static final H1 TOKEN_PREPARING = new H1(SectionState.ACTIVE);
		public static final H1 TOKEN_PREPARED = new H1();
		public static final H1 TOKEN_LOADING = new H1();
		public static final H1 TOKEN_LOADED = new H1();
		public static final H1 TOKEN_WANDERING = new H1();
		public static final H1 TOKEN_PLAYING = new H1();
		
		public static void finish_preparing() {
			H1 preparing = TOKEN_PREPARING;
			H1 prepared = TOKEN_PREPARED;
			H1 loading = TOKEN_LOADING;
			
			preparing.doLock();
			prepared.doLock();
			loading.doLock();
			
			preparing.doAdvance();
			prepared.doAdvance();
			loading.doAdvance();
			
			LOGGER_LIFECYCLE.info("Section PREPARING end and PREPARED, LOADING start");
			
			preparing.doUnlock();
			prepared.doUnlock();
			loading.doUnlock();
		}
		
		public static void finish_loading(boolean isServer) {
			H1 loading = TOKEN_LOADING;
			H1 loaded = TOKEN_LOADED;
			H1 wandering = TOKEN_WANDERING;
			
			loading.doLock();
			loaded.doLock();
			wandering.doLock();
			
			loading.doAdvance();
			loaded.doAdvance();
			wandering.doAdvance();
			
			LOGGER_LIFECYCLE.info("Section LOADING end and LOADED, WANDERING start");
			
			loading.doUnlock();
			loaded.doUnlock();
			wandering.doUnlock();
			
			if(isServer) {
				H1 playing = TOKEN_PLAYING;
				
				wandering.doLock();
				playing.doLock();
				
				wandering.doAdvance();
				playing.doAdvance();
				
				LOGGER_LIFECYCLE.info("Section WANDERING end and PLAYING start");
				
				wandering.doUnlock();
				playing.doUnlock();
			}
		}
		
		public static void on_client_playing_change() {
			H1 wandering = TOKEN_WANDERING;
			H1 playing = TOKEN_PLAYING;
			
			wandering.doLock();
			playing.doLock();
			
			wandering.doAdvance();
			playing.doAdvance();
			
			if(wandering.state == SectionState.ACTIVE)
				LOGGER_LIFECYCLE.info("Section PLAYING end and WANDERING start");
			else
				LOGGER_LIFECYCLE.info("Section WANDERING end and PLAYING start");
			
			wandering.doUnlock();
			playing.doUnlock();
		}
	}
	
	public static final class H2 {
		
		private H2() {}
		
		public static int custom_payload_pid(EnumPacketDirection direction) {
			return direction == EnumPacketDirection.CLIENTBOUND ? 24 : 9; //we don't need to get it dynamically since it was written in protocol
		}
		
		public static int custom_payload_maxload(EnumPacketDirection direction) {
			return direction == EnumPacketDirection.CLIENTBOUND ? 1048576 : 32767;
		}
		
		public static void shared_func_disconnect(ImplPCServer context) {
			context.manager.root.onLeft(context);
			
			LOGGER_USER.info("{}({}) the universal player logged out", context.identifier, context.name);
		}
	}
	
	public static final class H3 {
		
		public final Consumer<ContentDiff> subscriber;
		public final long tickSequence;
		
		public boolean initial;
		
		//used for creating prob
		public H3(Consumer<ContentDiff> subs) {
			this(subs, 0, 0);
		}
		
		public H3(Consumer<ContentDiff> subs, int tick, int freq) {
			this.subscriber = subs;
			this.tickSequence = (((long) (tick % freq)) << 32) | ((long) freq);
			
			this.initial = true;
		}
		
		public boolean shouldSend(int tick) {
			long seq = this.tickSequence;
			
			return tick % ((int) seq) == (int) (seq >> 32);
		}
		
		@Override
		public int hashCode() {
			return System.identityHashCode(this.subscriber);
		}
		
		@Override
		public boolean equals(Object obj) {
			return this.subscriber == ((H3) obj).subscriber;
		}
	}
	
	public static final class H4 {
		
		private H4() {}
		
		public static ByteBuf make_CB_init(OrderChunkNormal order, Chunk src) {
			PacketBuffer data = new PacketBuffer(Unpooled.buffer());
			ExtendedBlockStorage[] ebs = src.getBlockStorageArray();
			
			write_CN0(order, data);
			
			data.writeByte(0);
			
			int availableSections = 0;
			
			for(int i = 0; i < 16; i++) {
				if(ebs[i] != Chunk.NULL_BLOCK_STORAGE)
					availableSections |= 1 << i;
			}
			
			data.writeShort(availableSections);
			
			for(int i = 0; i < 16; i++) {
				if(ebs[i] != Chunk.NULL_BLOCK_STORAGE)
					ebs[i].getData().write(data);
			}
			
			return data.asReadOnly();
		}
		
		@SuppressWarnings("deprecation")
		public static ByteBuf make_CB_diff(OrderChunkNormal order, Chunk src, SmallShortSet changes) {
			PacketBuffer data = new PacketBuffer(Unpooled.buffer());
			
			write_CN0(order, data);
			
			data.writeByte(1);
			
			for(int i = changes.size(); i-- != 0;) {
				short s = changes.get(i);
				
				data.writeShort(s);
				data.writeVarInt(Block.BLOCK_STATE_IDS.get(src.getBlockState(s >> 12 & 15, s & 255, s >> 8 & 15)));
			}
			
			return data.asReadOnly();
		}
		
		public static ByteBuf make_WW(OrderWorldNormal order, float rainingStrength, float thunderingStrength) {
			ByteBuf buf = Unpooled.buffer();
			
			write_WN0(order, buf);
			
			buf.writeFloat(rainingStrength);
			buf.writeFloat(thunderingStrength);
			
			return buf.asReadOnly();
		}
		
		public static ByteBuf make_WDT(OrderWorldNormal order, WorldServer w) {
			ByteBuf buf = Unpooled.buffer();
			
			write_WN0(order, buf);
			
			buf.writeLong(w.getWorldTime());
			
			return buf.asReadOnly();
		}
		
		public static void write_CN(OrderChunkNormal src, ByteBuf dst) {
			writeVarInt(src.dimension().getId(), dst);
			
			dst.writeInt(src.chunkX());
			dst.writeInt(src.chunkZ());
		}
		
		private static void write_CN0(OrderChunkNormal src, ByteBuf dst) {
			writeVarInt(src.type().ordinal(), dst);
			write_CN(src, dst);
		}
		
		public static OrderChunkNormal read_CB(ByteBuf src) {
			return ContentType.orderBlock(DimensionType.getById(readVarInt(src)), src.readInt(), src.readInt());
		}
		
		public static void write_WN(OrderWorldNormal src, ByteBuf dst) {
			writeVarInt(src.dimension().getId(), dst);
		}
		
		private static void write_WN0(OrderWorldNormal src, ByteBuf dst) {
			writeVarInt(src.type().ordinal(), dst);
			write_WN(src, dst);
		}
		
		public static OrderWorldNormal read_WW(ByteBuf src) {
			return ContentType.orderWeather(DimensionType.getById(readVarInt(src)));
		}
		
		public static OrderWorldNormal read_WDT(ByteBuf src) {
			return ContentType.orderDaytime(DimensionType.getById(readVarInt(src)));
		}
		
		public static int readVarInt(ByteBuf src) {
			int i = 0;
			int j = 0;
			
			while(true) {
				byte b0 = src.readByte();
				i |= (b0 & 127) << j++ * 7;
				
				if(j > 5)
					throw new DecoderException("VarInt too big");
				
				if((b0 & 128) != 128)
					break;
			}
			
			return i;
		}
		
		public static void writeVarInt(int i, ByteBuf dst) {
			while((i & -128) != 0) {
				dst.writeByte(i & 127 | 128);
				i >>>= 7;
			}
			
			dst.writeByte(i);
		}
	}
	
	public static final class H5 {
		
		public float x;
		public float y;
		
		public boolean bool;
		
		public H5(float x, float y) {
			this.x = x;
			this.y = y;
		}
		
		public boolean equals(float x, float y) {
			return this.x == x && this.y == y;
		}
		
		public void set(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}
}
