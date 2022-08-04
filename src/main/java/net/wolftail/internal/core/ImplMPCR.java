package net.wolftail.internal.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.wolftail.api.RootPlayContextManager;
import net.wolftail.api.UniversalPlayerType;

public final class ImplMPCR implements RootPlayContextManager {
	
	private static final Logger logger = LogManager.getLogger("Wolftail/User");
	
	final MinecraftServer server;
	final Random rnd;
	
	private final ImmutableMap<UniversalPlayerType, ImplMPCS> subs;
	private final Map<UUID, ImplPCS> contexts;
	
	private File file_uniplayerType;
	private NBTTagCompound data_uniplayerType;
	
	public ImplMPCR(MinecraftServer arg0) {
		this.server = arg0;
		
		Builder<UniversalPlayerType, ImplMPCS> builder = ImmutableMap.builder();
		
		RegistryHolder.getRegistry().forEach(t -> {
			builder.put(t, new ImplMPCS((ImplUPT) t, this));
		});
		
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
	
	public ImplPCS login(NetworkManager connection, UUID id, String name) {
		ImplUPT type = this.determine_type(id, name);
		
		ImplPCS context = new ImplPCS(this.subs.get(type), id, name, connection);
		
		if (this.contexts.putIfAbsent(id, context) != null)
			throw new IllegalStateException("Duplicated join with ID " + id);
		context.subManager.current_load++;
		
		logger.info("{}({}) the universal player logged in with type {} and address {}", id, name,
				type.getRegistryName(), connection.getRemoteAddress());
		
		this.server.refreshStatusNextTick();
		
		return context;
	}
	
	public void logout(ImplPCS context) {
		this.contexts.remove(context.identifier);
		context.subManager.current_load--;
		
		logger.info("{}({}) the universal player logged out", context.identifier, context.name);
		
		this.server.refreshStatusNextTick();
	}
	
	public void loadDat() throws IOException {
		if ((this.file_uniplayerType = new File(this.server.worlds[0].getSaveHandler().getWorldDirectory(),
				"uniplayer-type.dat")).createNewFile()) {
			this.data_uniplayerType = new NBTTagCompound();
		} else
			this.data_uniplayerType = CompressedStreamTools.read(this.file_uniplayerType);
	}
	
	public void saveDat() throws IOException {
		CompressedStreamTools.safeWrite(this.data_uniplayerType, this.file_uniplayerType);
	}
	
	private ImplUPT determine_type(UUID id, String name) {
		NBTTagCompound data = this.data_uniplayerType;
		UniversalPlayerType type = null;
		
		NBTBase tag = data.getTag(name = id.toString());
		
		if (tag != null && tag.getId() == 8)
			type = RegistryHolder.getRegistry().getValue(new ResourceLocation(((NBTTagString) tag).getString()));
		
		if (type == null) {
			type = this.subs.values().asList().get(this.rnd.nextInt(this.subs.size())).type;
			
			data.setString(name, type.getRegistryName().toString());
		}
		
		return (ImplUPT) type;
	}
}
