package net.wolftail.internal.core.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.authlib.GameProfile;

import io.netty.handler.codec.DecoderException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketAdvancementInfo;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketCamera;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.network.play.server.SPacketCombatEvent;
import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketCooldown;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.network.play.server.SPacketDisplayObjective;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import net.minecraft.network.play.server.SPacketEntityHeadLook;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.network.play.server.SPacketPlaceGhostRecipe;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketRecipeBook;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.network.play.server.SPacketResourcePackSend;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketScoreboardObjective;
import net.minecraft.network.play.server.SPacketSelectAdvancementsTab;
import net.minecraft.network.play.server.SPacketServerDifficulty;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketSignEditorOpen;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnExperienceOrb;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketSpawnPainting;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.network.play.server.SPacketSpawnPosition;
import net.minecraft.network.play.server.SPacketStatistics;
import net.minecraft.network.play.server.SPacketTabComplete;
import net.minecraft.network.play.server.SPacketTeams;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.network.play.server.SPacketUnloadChunk;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.network.play.server.SPacketUpdateScore;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.network.play.server.SPacketUseBed;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.network.play.server.SPacketWindowProperty;
import net.minecraft.network.play.server.SPacketWorldBorder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher.ConnectionType;
import net.wolftail.api.UniversalPlayerType;
import net.wolftail.internal.core.ExtCoreMinecraft;
import net.wolftail.internal.core.ImplUPT;
import net.wolftail.internal.core.RegistryHolder;

public final class TransientPacketListener implements INetHandlerPlayClient {
	
	private static final Logger logger = LogManager.getLogger("Wolftail/Network");
	
	private final NetHandlerPlayClient prevHandler;
	private final ConnectionType connectionType;
	
	public TransientPacketListener(NetHandlerPlayClient h, ConnectionType t) {
		this.prevHandler = h;
		this.connectionType = t;
	}
	
	private void pass(ImplUPT type, Packet<INetHandlerPlayClient> unexpected) {
		if (unexpected != null) {
			logger.warn("Expecting a type notify packet, maybe the server dosen't install wolftail. Assuming '"
					+ UniversalPlayerType.TYPE_PLAYER_ID + "'.");
			
			type = (ImplUPT) UniversalPlayerType.TYPE_PLAYER;
		}
		
		GameProfile p = this.prevHandler.getGameProfile();
		NetworkManager c = this.prevHandler.getNetworkManager();
		
		// we now should in netty thread
		((ExtCoreMinecraft) Minecraft.getMinecraft()).wolftail_loadContext(type, p.getId(), p.getName(), c);
		
		if (type.isSteve()) {
			MinecraftForge.EVENT_BUS
					.post(new FMLNetworkEvent.ClientConnectedToServerEvent(c, this.connectionType.name()));
			
			c.setNetHandler(this.prevHandler);
		}
		
		if (unexpected != null)
			unexpected.processPacket(this.prevHandler);
	}
	
	@Override
	public void handleCustomPayload(SPacketCustomPayload packetIn) {
		if (packetIn.getChannelName().equals("WT|TN")) {
			ResourceLocation typeId = packetIn.getBufferData().readResourceLocation();
			ImplUPT type;
			
			if (packetIn.getBufferData().isReadable())
				throw new DecoderException();
			
			if ((type = (ImplUPT) RegistryHolder.getRegistry().getValue(typeId)) == null)
				throw new IllegalStateException("Unknow universal player type " + typeId);
			
			this.pass(type, null);
		} else
			this.pass(null, packetIn);
	}
	
	@Override
	public void onDisconnect(ITextComponent reason) {
		this.prevHandler.onDisconnect(reason);
	}
	
	@Override
	public void handleDisconnect(SPacketDisconnect packetIn) {
		this.prevHandler.handleDisconnect(packetIn);
	}
	
	@Override
	public void handleKeepAlive(SPacketKeepAlive packetIn) {
		this.prevHandler.handleKeepAlive(packetIn);
	}
	
	@Override
	public void handleSpawnObject(SPacketSpawnObject packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleSpawnExperienceOrb(SPacketSpawnExperienceOrb packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleSpawnGlobalEntity(SPacketSpawnGlobalEntity packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleSpawnMob(SPacketSpawnMob packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleScoreboardObjective(SPacketScoreboardObjective packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleSpawnPainting(SPacketSpawnPainting packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleSpawnPlayer(SPacketSpawnPlayer packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleAnimation(SPacketAnimation packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleStatistics(SPacketStatistics packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleRecipeBook(SPacketRecipeBook packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleBlockBreakAnim(SPacketBlockBreakAnim packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleSignEditorOpen(SPacketSignEditorOpen packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleUpdateTileEntity(SPacketUpdateTileEntity packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleBlockAction(SPacketBlockAction packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleBlockChange(SPacketBlockChange packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleChat(SPacketChat packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleTabComplete(SPacketTabComplete packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleMultiBlockChange(SPacketMultiBlockChange packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleMaps(SPacketMaps packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleConfirmTransaction(SPacketConfirmTransaction packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleCloseWindow(SPacketCloseWindow packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleWindowItems(SPacketWindowItems packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleOpenWindow(SPacketOpenWindow packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleWindowProperty(SPacketWindowProperty packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleSetSlot(SPacketSetSlot packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleUseBed(SPacketUseBed packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleEntityStatus(SPacketEntityStatus packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleEntityAttach(SPacketEntityAttach packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleSetPassengers(SPacketSetPassengers packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleExplosion(SPacketExplosion packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleChangeGameState(SPacketChangeGameState packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleChunkData(SPacketChunkData packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void processChunkUnload(SPacketUnloadChunk packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleEffect(SPacketEffect packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleJoinGame(SPacketJoinGame packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleEntityMovement(SPacketEntity packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handlePlayerPosLook(SPacketPlayerPosLook packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleParticles(SPacketParticles packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handlePlayerAbilities(SPacketPlayerAbilities packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handlePlayerListItem(SPacketPlayerListItem packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleDestroyEntities(SPacketDestroyEntities packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleRemoveEntityEffect(SPacketRemoveEntityEffect packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleRespawn(SPacketRespawn packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleEntityHeadLook(SPacketEntityHeadLook packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleHeldItemChange(SPacketHeldItemChange packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleDisplayObjective(SPacketDisplayObjective packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleEntityMetadata(SPacketEntityMetadata packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleEntityVelocity(SPacketEntityVelocity packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleEntityEquipment(SPacketEntityEquipment packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleSetExperience(SPacketSetExperience packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleUpdateHealth(SPacketUpdateHealth packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleTeams(SPacketTeams packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleUpdateScore(SPacketUpdateScore packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleSpawnPosition(SPacketSpawnPosition packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleTimeUpdate(SPacketTimeUpdate packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleSoundEffect(SPacketSoundEffect packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleCustomSound(SPacketCustomSound packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleCollectItem(SPacketCollectItem packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleEntityTeleport(SPacketEntityTeleport packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleEntityProperties(SPacketEntityProperties packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleEntityEffect(SPacketEntityEffect packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleCombatEvent(SPacketCombatEvent packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleServerDifficulty(SPacketServerDifficulty packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleCamera(SPacketCamera packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleWorldBorder(SPacketWorldBorder packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleTitle(SPacketTitle packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handlePlayerListHeaderFooter(SPacketPlayerListHeaderFooter packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleResourcePack(SPacketResourcePackSend packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleUpdateBossInfo(SPacketUpdateBossInfo packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleCooldown(SPacketCooldown packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleMoveVehicle(SPacketMoveVehicle packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleAdvancementInfo(SPacketAdvancementInfo packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void handleSelectAdvancementsTab(SPacketSelectAdvancementsTab packetIn) {
		this.pass(null, packetIn);
	}
	
	@Override
	public void func_194307_a(SPacketPlaceGhostRecipe p_194307_1_) {
		this.pass(null, p_194307_1_);
	}
}
