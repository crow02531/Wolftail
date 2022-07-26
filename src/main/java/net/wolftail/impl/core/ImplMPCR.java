package net.wolftail.impl.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.wolftail.api.RootPlayContextManager;
import net.wolftail.api.UniversalPlayerType;
import net.wolftail.api.UniversalPlayerTypeRegistry;

public final class ImplMPCR implements RootPlayContextManager {
	
	private static final Logger logger = LogManager.getLogger("Wolftail/User");
	
	final MinecraftServer server;
	final Random rnd;
	
	private final Map<ImplUPT, ImplMPCS>	subs;
	private final Map<UUID, ImplPCS>		contexts;
	
	private File file_uniplayerType;
	private CompoundTag data_uniplayerType;
	
	@SuppressWarnings("unchecked")
	public ImplMPCR(MinecraftServer arg0) {
		this.server = arg0;
		
		ImmutableMap.Builder<ImplUPT, ImplMPCS> builder = ImmutableMap.builder();
		
		for(ImplUPT t : (Set<ImplUPT>) (Object) UniversalPlayerTypeRegistry.INSTANCE.asMap().values())
			builder.put(t, new ImplMPCS(t, this));
		
		this.subs = builder.build();
		this.contexts = new HashMap<>();
		
		this.rnd = new Random();
	}
	
	@Override
	public MinecraftServer server() {
		return this.server;
	}
	
	@Override
	public ImplPCS contextFor(UUID playId) {
		return this.contexts.get(playId);
	}
	
	@Override
	public int currentLoad() {
		return this.contexts.size();
	}
	
	@Override
	public int maxLoad() {
		return this.server.getMaxPlayers();
	}
	
	@Override
	public ImplMPCS subManager(UniversalPlayerType type) {
		return this.subs.get(type);
	}
	
	public ImplPCS login(Connection connection, UUID id, String name) {
		ImplUPT type = this.determine_type(id, name);
		
		ImplPCS context = new ImplPCS(this.subs.get(type), id, name, connection);
		
		if(this.contexts.putIfAbsent(id, context) != null)
			throw new IllegalStateException("Duplicated join with ID " + id);
		context.subManager.current_load++;
		
		logger.info("{}({}) the universal player logged in with type {} and address {}", id, name, type.registeringId(), connection.getRemoteAddress());
		
		return context;
	}
	
	public void logout(ImplPCS context) {
		this.contexts.remove(context.identifier);
		context.subManager.current_load--;
		
		logger.info("{}({}) the universal player logged out", context.identifier, context.name);
	}
	
	public void loadDat() throws IOException {
		if((this.file_uniplayerType = this.server.getWorldPath(LevelResource.ROOT).resolve("uniplayer-type.dat").toFile()).createNewFile()) {
			this.data_uniplayerType = new CompoundTag();
		} else this.data_uniplayerType = NbtIo.readCompressed(this.file_uniplayerType);
	}
	
	public void saveDat() throws IOException {
		NbtIo.writeCompressed(this.data_uniplayerType, this.file_uniplayerType);
	}
	
	private ImplUPT determine_type(UUID id, String name) {
		CompoundTag data = this.data_uniplayerType;
		UniversalPlayerType type = null;
		
		Tag tag = data.get(name = id.toString());
		
		if(tag != null && tag.getId() == Tag.TAG_STRING)
			type = UniversalPlayerTypeRegistry.INSTANCE.byId(new ResourceLocation(tag.getAsString()));
		
		if(type == null) {
			type = UniversalPlayerTypeRegistry.INSTANCE.getRandomType(this.rnd);
			
			data.putString(name, type.registeringId().toString());
		}
		
		return (ImplUPT) type;
	}
}
