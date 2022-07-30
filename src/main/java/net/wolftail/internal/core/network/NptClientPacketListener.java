package net.wolftail.internal.core.network;

import net.minecraft.network.NetworkManager;
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
import net.minecraft.util.text.ITextComponent;

public final class NptClientPacketListener extends NptPacketListener implements INetHandlerPlayClient {
	
	private final NetworkManager connection;
	
	public NptClientPacketListener(NetworkManager c) {
		this.connection = c;
	}
	
	@Override
	public void onDisconnect(ITextComponent reason) {
		// NOOP - see MixinMinecraft.doGameLoop
	}
	
	@Override
	public void handleCustomPayload(SPacketCustomPayload packetIn) {
		check0(packetIn.getChannelName());
		
		this.handlePayload(packetIn.getBufferData());
	}
	
	@Override
	public void handleDisconnect(SPacketDisconnect packetIn) {
		this.connection.closeChannel(packetIn.getReason());
	}
	
	@Override
	public void handleKeepAlive(SPacketKeepAlive packetIn) {
		// NOOP
	}
	
	@Override
	public void handleSpawnObject(SPacketSpawnObject packetIn) {
		throw0();
	}
	
	@Override
	public void handleSpawnExperienceOrb(SPacketSpawnExperienceOrb packetIn) {
		throw0();
	}
	
	@Override
	public void handleSpawnGlobalEntity(SPacketSpawnGlobalEntity packetIn) {
		throw0();
	}
	
	@Override
	public void handleSpawnMob(SPacketSpawnMob packetIn) {
		throw0();
	}
	
	@Override
	public void handleScoreboardObjective(SPacketScoreboardObjective packetIn) {
		throw0();
	}
	
	@Override
	public void handleSpawnPainting(SPacketSpawnPainting packetIn) {
		throw0();
	}
	
	@Override
	public void handleSpawnPlayer(SPacketSpawnPlayer packetIn) {
		throw0();
	}
	
	@Override
	public void handleAnimation(SPacketAnimation packetIn) {
		throw0();
	}
	
	@Override
	public void handleStatistics(SPacketStatistics packetIn) {
		throw0();
	}
	
	@Override
	public void handleRecipeBook(SPacketRecipeBook packetIn) {
		throw0();
	}
	
	@Override
	public void handleBlockBreakAnim(SPacketBlockBreakAnim packetIn) {
		throw0();
	}
	
	@Override
	public void handleSignEditorOpen(SPacketSignEditorOpen packetIn) {
		throw0();
	}
	
	@Override
	public void handleUpdateTileEntity(SPacketUpdateTileEntity packetIn) {
		throw0();
	}
	
	@Override
	public void handleBlockAction(SPacketBlockAction packetIn) {
		throw0();
	}
	
	@Override
	public void handleBlockChange(SPacketBlockChange packetIn) {
		throw0();
	}
	
	@Override
	public void handleChat(SPacketChat packetIn) {
		throw0();
	}
	
	@Override
	public void handleTabComplete(SPacketTabComplete packetIn) {
		throw0();
	}
	
	@Override
	public void handleMultiBlockChange(SPacketMultiBlockChange packetIn) {
		throw0();
	}
	
	@Override
	public void handleMaps(SPacketMaps packetIn) {
		throw0();
	}
	
	@Override
	public void handleConfirmTransaction(SPacketConfirmTransaction packetIn) {
		throw0();
	}
	
	@Override
	public void handleCloseWindow(SPacketCloseWindow packetIn) {
		throw0();
	}
	
	@Override
	public void handleWindowItems(SPacketWindowItems packetIn) {
		throw0();
	}
	
	@Override
	public void handleOpenWindow(SPacketOpenWindow packetIn) {
		throw0();
	}
	
	@Override
	public void handleWindowProperty(SPacketWindowProperty packetIn) {
		throw0();
	}
	
	@Override
	public void handleSetSlot(SPacketSetSlot packetIn) {
		throw0();
	}
	
	@Override
	public void handleUseBed(SPacketUseBed packetIn) {
		throw0();
	}
	
	@Override
	public void handleEntityStatus(SPacketEntityStatus packetIn) {
		throw0();
	}
	
	@Override
	public void handleEntityAttach(SPacketEntityAttach packetIn) {
		throw0();
	}
	
	@Override
	public void handleSetPassengers(SPacketSetPassengers packetIn) {
		throw0();
	}
	
	@Override
	public void handleExplosion(SPacketExplosion packetIn) {
		throw0();
	}
	
	@Override
	public void handleChangeGameState(SPacketChangeGameState packetIn) {
		throw0();
	}
	
	@Override
	public void handleChunkData(SPacketChunkData packetIn) {
		throw0();
	}
	
	@Override
	public void processChunkUnload(SPacketUnloadChunk packetIn) {
		throw0();
	}
	
	@Override
	public void handleEffect(SPacketEffect packetIn) {
		throw0();
	}
	
	@Override
	public void handleJoinGame(SPacketJoinGame packetIn) {
		throw0();
	}
	
	@Override
	public void handleEntityMovement(SPacketEntity packetIn) {
		throw0();
	}
	
	@Override
	public void handlePlayerPosLook(SPacketPlayerPosLook packetIn) {
		throw0();
	}
	
	@Override
	public void handleParticles(SPacketParticles packetIn) {
		throw0();
	}
	
	@Override
	public void handlePlayerAbilities(SPacketPlayerAbilities packetIn) {
		throw0();
	}
	
	@Override
	public void handlePlayerListItem(SPacketPlayerListItem packetIn) {
		throw0();
	}
	
	@Override
	public void handleDestroyEntities(SPacketDestroyEntities packetIn) {
		throw0();
	}
	
	@Override
	public void handleRemoveEntityEffect(SPacketRemoveEntityEffect packetIn) {
		throw0();
	}
	
	@Override
	public void handleRespawn(SPacketRespawn packetIn) {
		throw0();
	}
	
	@Override
	public void handleEntityHeadLook(SPacketEntityHeadLook packetIn) {
		throw0();
	}
	
	@Override
	public void handleHeldItemChange(SPacketHeldItemChange packetIn) {
		throw0();
	}
	
	@Override
	public void handleDisplayObjective(SPacketDisplayObjective packetIn) {
		throw0();
	}
	
	@Override
	public void handleEntityMetadata(SPacketEntityMetadata packetIn) {
		throw0();
	}
	
	@Override
	public void handleEntityVelocity(SPacketEntityVelocity packetIn) {
		throw0();
	}
	
	@Override
	public void handleEntityEquipment(SPacketEntityEquipment packetIn) {
		throw0();
	}
	
	@Override
	public void handleSetExperience(SPacketSetExperience packetIn) {
		throw0();
	}
	
	@Override
	public void handleUpdateHealth(SPacketUpdateHealth packetIn) {
		throw0();
	}
	
	@Override
	public void handleTeams(SPacketTeams packetIn) {
		throw0();
	}
	
	@Override
	public void handleUpdateScore(SPacketUpdateScore packetIn) {
		throw0();
	}
	
	@Override
	public void handleSpawnPosition(SPacketSpawnPosition packetIn) {
		throw0();
	}
	
	@Override
	public void handleTimeUpdate(SPacketTimeUpdate packetIn) {
		throw0();
	}
	
	@Override
	public void handleSoundEffect(SPacketSoundEffect packetIn) {
		throw0();
	}
	
	@Override
	public void handleCustomSound(SPacketCustomSound packetIn) {
		throw0();
	}
	
	@Override
	public void handleCollectItem(SPacketCollectItem packetIn) {
		throw0();
	}
	
	@Override
	public void handleEntityTeleport(SPacketEntityTeleport packetIn) {
		throw0();
	}
	
	@Override
	public void handleEntityProperties(SPacketEntityProperties packetIn) {
		throw0();
	}
	
	@Override
	public void handleEntityEffect(SPacketEntityEffect packetIn) {
		throw0();
	}
	
	@Override
	public void handleCombatEvent(SPacketCombatEvent packetIn) {
		throw0();
	}
	
	@Override
	public void handleServerDifficulty(SPacketServerDifficulty packetIn) {
		throw0();
	}
	
	@Override
	public void handleCamera(SPacketCamera packetIn) {
		throw0();
	}
	
	@Override
	public void handleWorldBorder(SPacketWorldBorder packetIn) {
		throw0();
	}
	
	@Override
	public void handleTitle(SPacketTitle packetIn) {
		throw0();
	}
	
	@Override
	public void handlePlayerListHeaderFooter(SPacketPlayerListHeaderFooter packetIn) {
		throw0();
	}
	
	@Override
	public void handleResourcePack(SPacketResourcePackSend packetIn) {
		throw0();
	}
	
	@Override
	public void handleUpdateBossInfo(SPacketUpdateBossInfo packetIn) {
		throw0();
	}
	
	@Override
	public void handleCooldown(SPacketCooldown packetIn) {
		throw0();
	}
	
	@Override
	public void handleMoveVehicle(SPacketMoveVehicle packetIn) {
		throw0();
	}
	
	@Override
	public void handleAdvancementInfo(SPacketAdvancementInfo packetIn) {
		throw0();
	}
	
	@Override
	public void handleSelectAdvancementsTab(SPacketSelectAdvancementsTab packetIn) {
		throw0();
	}
	
	@Override
	public void func_194307_a(SPacketPlaceGhostRecipe p_194307_1_) {
		throw0();
	}
}
