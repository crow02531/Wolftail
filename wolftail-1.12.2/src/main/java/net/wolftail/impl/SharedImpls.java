package net.wolftail.impl;

import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SectionState;
import net.wolftail.util.tracker.ContentDiff;
import net.wolftail.util.tracker.SubscribeOrder;

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
	
	public static void clinit(Class<?> klass) {
		try {
			Class.forName(klass.getName(), true, klass.getClassLoader());
		} catch(ClassNotFoundException e) {
			//never happen
		}
	}
	
	public static final class H0 {
		
		private H0() {}
		
		public static Thread regular_dedicated_server_host; //dedicatedServer has two host thread, the second one is called regular by us
	}
	
	public static abstract class H1 {
		
		public abstract void doLock();
		public abstract void doAdvance();
		public abstract void doUnlock();
		
		public abstract SectionState currentState();
		
		static {
			clinit(GameSection.class);
		}
		
		public static H1 token_preparing;
		public static H1 token_prepared;
		public static H1 token_loading;
		public static H1 token_loaded;
		public static H1 token_wandering;
		public static H1 token_playing;
		
		public static void finish_preparing() {
			H1 preparing = token_preparing;
			H1 prepared = token_prepared;
			H1 loading = token_loading;
			
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
			H1 loading = token_loading;
			H1 loaded = token_loaded;
			H1 wandering = token_wandering;
			
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
				H1 playing = token_playing;
				
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
			H1 wandering = token_wandering;
			H1 playing = token_playing;
			
			wandering.doLock();
			playing.doLock();
			
			wandering.doAdvance();
			playing.doAdvance();
			
			if(wandering.currentState() == SectionState.ACTIVE)
				LOGGER_LIFECYCLE.info("Section PLAYING end and WANDERING start");
			else
				LOGGER_LIFECYCLE.info("Section WANDERING end and PLAYING start");
			
			wandering.doUnlock();
			playing.doUnlock();
		}
	}
	
	public static final class H2 {
		
		private H2() {}
		
		static {
			clinit(ContentDiff.class);
		}
		
		public static Function<SubscribeOrder, ContentDiff> content_diff_factory;
		
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
}
