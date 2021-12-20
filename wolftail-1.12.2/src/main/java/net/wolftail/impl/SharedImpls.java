package net.wolftail.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.server.MinecraftServer;
import net.wolftail.api.lifecycle.BuiltInSection;
import net.wolftail.api.lifecycle.SectionState;
import net.wolftail.api.lifecycle.SectionToken;

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
	
	public static class Holder0 {
		
		public static Thread regular_dedicated_server_host; //dedicatedServer has two host thread, the second one is called regular by us
	}
	
	public static class Holder1 {
		
		static {
			clinit(BuiltInSection.class);
		}
		
		public static SectionToken token_preparing;
		public static SectionToken token_prepared;
		public static SectionToken token_loading;
		public static SectionToken token_loaded;
		public static SectionToken token_wandering;
		public static SectionToken token_playing;
		
		public static void finish_preparing() {
			SectionToken preparing = token_preparing;
			SectionToken prepared = token_prepared;
			SectionToken loading = token_loading;
			
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
		
		public static void finish_loading(boolean isClient) {
			SectionToken loading = token_loading;
			SectionToken loaded = token_loaded;
			
			if(isClient) {
				SectionToken wandering = token_wandering;
				
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
			} else {
				loading.doLock();
				loaded.doLock();
				
				loading.doAdvance();
				loaded.doAdvance();
				
				LOGGER_LIFECYCLE.info("Section LOADING end and LOADED start");
				
				loading.doUnlock();
				loaded.doUnlock();
			}
		}
		
		public static void on_client_playing_change() {
			SectionToken wandering = token_wandering;
			SectionToken playing = token_playing;
			
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
}
