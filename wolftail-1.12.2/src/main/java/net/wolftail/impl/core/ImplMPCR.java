package net.wolftail.impl.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

public final class ImplMPCR implements RootPlayContextManager {
	
	private static final Logger logger = LogManager.getLogger("wolftail/user");
	
	final MinecraftServer server;
	
	private final Map<ImplUPT, ImplMPCS>	subs;
	private final Map<UUID, ImplPC.Server>	contexts;
	
	private Set<SubPlayContextManager>		view_set_m;
	private Set<ServerPlayContext>			view_set_c;
	
	private final Random rnd;
	
	private final File file_uniplayerType;
	private NBTTagCompound data_uniplayerType;
	
	public ImplMPCR(MinecraftServer arg0) throws IOException {
		this.server = arg0;
		
		@SuppressWarnings("unchecked")
		Set<ImplUPT> allTypes = (Set<ImplUPT>) (Object) UniversalPlayerTypeRegistry.INSTANCE.asMap().values();
		ImmutableMap.Builder<ImplUPT, ImplMPCS> builder = ImmutableMap.builder();
		
		for(ImplUPT t : allTypes)
			builder.put(t, new ImplMPCS(t, this));
		
		this.subs = builder.build();
		this.contexts = new HashMap<>();
		
		if((this.file_uniplayerType = new File(arg0.worlds[0].getSaveHandler().getWorldDirectory(), "uniplayer-type.dat")).createNewFile()) {
			this.data_uniplayerType = new NBTTagCompound();
		} else this.data_uniplayerType = CompressedStreamTools.readCompressed(new FileInputStream(this.file_uniplayerType));
		
		this.rnd = new Random();
	}
	
	@Override
	public MinecraftServer server() {
		return this.server;
	}
	
	@Override
	public ImplPC.Server contextFor(UUID playId) {
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
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<SubPlayContextManager> asManagerSet() {
		Set<SubPlayContextManager> ret = this.view_set_m;
		
		return ret != null ? ret : (this.view_set_m = (Set<SubPlayContextManager>) (Object) wrap(this.subs.values()));
	}
	
	@Override
	public Set<ServerPlayContext> asContextSet() {
		Set<ServerPlayContext> ret = this.view_set_c;
		
		return ret != null ? ret : (this.view_set_c = Collections.unmodifiableSet(wrap(this.contexts.values())));
	}
	
	public ImplPC.Server login(NetworkManager connection, UUID id, String name) {
		ImplMPCS manager = this.subs.get(this.determine_type(id, name));
		ImplPC.Server context = new ImplPC.Server(manager, id, name, connection);
		
		if(this.contexts.putIfAbsent(id, context) != null)
			throw new IllegalStateException("Duplicated join with ID " + id);
		manager.current_load++;
		
		logger.info("{}({}) the universal player logged in with type {} and address {}", id, name, manager.type.registeringId(), connection.getRemoteAddress());
		
		return context;
	}
	
	public void logout(ImplPC.Server context) {
		this.contexts.remove(context.identifier);
		context.manager.current_load--;
		
		logger.info("{}({}) the universal player logged out", context.identifier, context.name);
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
			type = UniversalPlayerTypeRegistry.INSTANCE.getRandomType(this.rnd);
			
			data.setString(name, type.registeringId().toString());
		}
		
		return type;
	}
	
	private static <E> Set<E> wrap(Collection<E> wrapped) {
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
}
