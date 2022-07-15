package net.wolftail.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ImmutableMap;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.wolftail.api.RootPlayContextManager;
import net.wolftail.api.ServerPlayContext;
import net.wolftail.api.SubPlayContextManager;
import net.wolftail.api.UniversalPlayerType;
import net.wolftail.api.UniversalPlayerTypeRegistry;

public final class ImplMPCRoot implements RootPlayContextManager {
	
	final MinecraftServer server;
	
	private final Map<ImplUPT, ImplMPCSub>	subs;
	private final Map<UUID, ImplPCServer>	contexts;
	
	private Set<SubPlayContextManager>		view_set_m;
	private Set<ServerPlayContext>			view_set_c;
	
	private final File file_uniplayerType;
	private NBTTagCompound data_uniplayerType;
	
	public ImplMPCRoot(MinecraftServer arg0) throws IOException {
		this.server = arg0;
		
		Set<ImplUPT> allTypes = SharedImpls.as(UniversalPlayerTypeRegistry.INSTANCE.asMap().values());
		ImmutableMap.Builder<ImplUPT, ImplMPCSub> builder = ImmutableMap.builder();
		
		for(ImplUPT t : allTypes)
			builder.put(t, new ImplMPCSub(t, this));
		
		this.subs = builder.build();
		this.contexts = new HashMap<>();
		
		if((this.file_uniplayerType = new File(arg0.worlds[0].getSaveHandler().getWorldDirectory(), "uniplayer-type.dat")).createNewFile()) {
			this.data_uniplayerType = new NBTTagCompound();
		} else this.data_uniplayerType = CompressedStreamTools.readCompressed(new FileInputStream(this.file_uniplayerType));
	}
	
	@Override
	public MinecraftServer server() {
		return this.server;
	}
	
	@Override
	public ImplPCServer contextFor(UUID playId) {
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
	public SubPlayContextManager subManager(UniversalPlayerType type) {
		return this.subs.get(type);
	}
	
	@Override
	public Set<SubPlayContextManager> asManagerSet() {
		Set<SubPlayContextManager> ret = this.view_set_m;
		
		return ret != null ? ret : (this.view_set_m = SharedImpls.as(SharedImpls.wrap(this.subs.values())));
	}
	
	@Override
	public Set<ServerPlayContext> asContextSet() {
		Set<ServerPlayContext> ret = this.view_set_c;
		
		return ret != null ? ret : (this.view_set_c = Collections.unmodifiableSet(SharedImpls.wrap(this.contexts.values())));
	}
	
	public ImplPCServer join(NetworkManager connection, UUID id, String name) {
		ImplMPCSub manager = this.subs.get(this.determine_type(id, name));
		ImplPCServer context = new ImplPCServer(manager, id, name, connection);
		
		if(this.contexts.putIfAbsent(id, context) != null)
			throw new IllegalStateException("Duplicated join with ID " + id);
		
		manager.current_load++;
		
		return context;
	}
	
	public void onLeft(ImplPCServer context) {
		this.contexts.remove(context.identifier);
		
		context.manager.current_load--;
	}
	
	public void onServerStopping() throws FileNotFoundException, IOException {
		CompressedStreamTools.writeCompressed(this.data_uniplayerType, new FileOutputStream(this.file_uniplayerType));
	}
	
	private UniversalPlayerType determine_type(UUID id, String name) {
		NBTTagCompound data = this.data_uniplayerType;
		UniversalPlayerType type = null;
		
		if(data.hasKey(name = id.toString()))
			type = UniversalPlayerTypeRegistry.INSTANCE.registeredAt(new ResourceLocation(data.getString(name)));
		
		if(type == null) {
			type = UniversalPlayerTypeRegistry.INSTANCE.getRandomType(SharedImpls.as(this.server).wolftail_random());
			
			data.setString(name, type.registeringId().toString());
		}
		
		return type;
	}
}
