package net.wolftail.internal.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.wolftail.api.PlayContext;
import net.wolftail.api.RootPlayContextManager;
import net.wolftail.api.UniversalPlayerType;
import net.wolftail.util.MoreServers;

public final class ImplMPCR implements RootPlayContextManager {
	
	private static final Logger logger = LogManager.getLogger("Wolftail/User");
	
	final MinecraftServer server;
	final Random rnd;
	
	private final ImmutableMap<UniversalPlayerType, ImplMPCS> type2subs;
	private final Map<UUID, ImplPCS> id2contexts;
	
	private final List<ImplPCS> contexts;
	
	private File file_uniplayerType;
	private NBTTagCompound data_uniplayerType;
	
	public ImplMPCR(MinecraftServer arg0) {
		this.server = arg0;
		
		Builder<UniversalPlayerType, ImplMPCS> builder = ImmutableMap.builder();
		
		RegistryHolder.getRegistry().forEach(t -> {
			builder.put(t, new ImplMPCS((ImplUPT) t, this));
		});
		
		this.type2subs = builder.build();
		this.id2contexts = new HashMap<>();
		
		this.contexts = new ArrayList<>();
		
		this.rnd = new Random();
	}
	
	@Override
	public MinecraftServer server() {
		return this.server;
	}
	
	@Override
	public ImplPCS contextFor(UUID playId) {
		return this.id2contexts.get(playId);
	}
	
	public ImplPCS contextAt(int i) {
		return this.contexts.get(i);
	}
	
	@Override
	public int currentLoad() {
		return this.id2contexts.size();
	}
	
	@Override
	public int maxLoad() {
		return this.server.getMaxPlayers();
	}
	
	@Override
	public void sendChat(ChatType type, ITextComponent text, Predicate<PlayContext> filter) {
		this.sendChat(new SPacketChat(text, type), filter);
	}
	
	public void sendChat(SPacketChat p, Predicate<PlayContext> f) {
		for (ImplPCS pcs : this.contexts) {
			if (f.test(pcs))
				pcs.connection.sendPacket(p);
		}
	}
	
	@Override
	public ImplMPCS subManager(UniversalPlayerType type) {
		return this.type2subs.get(type);
	}
	
	public ImplPCS login(NetworkManager connection, UUID id, String name) {
		ImplUPT type = this.determine_type(id, name);
		ImplPCS context = new ImplPCS(this.type2subs.get(type), id, name, connection);
		
		if (this.id2contexts.putIfAbsent(id, context) != null)
			throw new IllegalStateException("Duplicated join with ID " + id);
		
		context.subManager.current_load++;
		
		this.contexts.add(context);
		this.server.refreshStatusNextTick();
		
		logger.info("{}({}) the universal player logged in with type {} and address {}", id, name,
				type.getRegistryName(), connection.getRemoteAddress());
		
		return context;
	}
	
	public void logout(ImplPCS context) {
		this.id2contexts.remove(context.identifier);
		this.contexts.remove(context);
		this.server.refreshStatusNextTick();
		
		context.subManager.current_load--;
		
		logger.info("{}({}) the universal player logged out", context.identifier, context.name);
	}
	
	public void loadDat() throws IOException {
		if ((this.file_uniplayerType = new File(MoreServers.dirOf(this.server), "uniplayer-type.dat"))
				.createNewFile()) {
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
			type = this.type2subs.values().asList().get(this.rnd.nextInt(this.type2subs.size())).type;
			
			data.setString(name, type.getRegistryName().toString());
		}
		
		return (ImplUPT) type;
	}
}
