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
import net.wolftail.util.tracker.OrderChunkBlock;

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
		
		public Consumer<ContentDiff> subscriber;
		
		public boolean initial;
		
		public H3(Consumer<ContentDiff> arg) {
			this.subscriber = arg;
			
			this.initial = true;
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
		
		public static ByteBuf make_CB_Init(OrderChunkBlock order, Chunk src) {
			PacketBuffer data = new PacketBuffer(Unpooled.buffer());
			ExtendedBlockStorage[] ebs = src.getBlockStorageArray();
			
			data.writeVarInt(order.type().ordinal());
			write_CB(order, data);
			
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
		public static ByteBuf make_CB_Diff(OrderChunkBlock order, Chunk src) {
			PacketBuffer data = new PacketBuffer(Unpooled.buffer());
			ExtensionsChunk ec = as(src);
			
			data.writeVarInt(order.type().ordinal());
			write_CB(order, data);
			
			data.writeByte(1);
			
			for(short s : ec.wolftail_changedBlocks()) {
				data.writeShort(s);
				data.writeVarInt(Block.BLOCK_STATE_IDS.get(src.getBlockState(s >> 12 & 15, s & 255, s >> 8 & 15)));
			}
			
			return data.asReadOnly();
		}
		
		public static void write_CB(OrderChunkBlock src, ByteBuf dst) {
			writeVarInt(src.dimension().getId(), dst);
			
			dst.writeInt(src.chunkX());
			dst.writeInt(src.chunkZ());
		}
		
		public static OrderChunkBlock read_CB(ByteBuf src) {
			return ContentType.orderBlock(DimensionType.getById(readVarInt(src)), src.readInt(), src.readInt());
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
}
