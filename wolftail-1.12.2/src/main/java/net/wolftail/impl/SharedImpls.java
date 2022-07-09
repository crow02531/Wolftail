package net.wolftail.impl;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.wolftail.api.lifecycle.SectionState;
import net.wolftail.util.tracker.ContentDiff;

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
	
	public static final class H0 {
		
		private H0() {}
		
		public static Thread regular_dedicated_server_host; //dedicatedServer has two host thread, the second one is called regular by us
	}
	
	public static final class H1 {
		
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
}
