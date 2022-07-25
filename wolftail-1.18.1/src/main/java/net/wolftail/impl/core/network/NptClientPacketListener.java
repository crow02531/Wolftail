package net.wolftail.impl.core.network;

import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.*;

public final class NptClientPacketListener
extends NptPacketListener implements ClientGamePacketListener {
	
	private final Connection connection;
	
	public NptClientPacketListener(Connection c) {
		this.connection = c;
	}
	
	@Override
	public void onDisconnect(Component var1) {
		//NOOP - see MixinMinecraft.doGameLoop
	}
	
	@Override
	public void handleCustomPayload(ClientboundCustomPayloadPacket var1) {
		check0(var1.getIdentifier());
		
		this.handlePayload(var1.getData());
	}
	
	@Override
	public void handleDisconnect(ClientboundDisconnectPacket var1) {
		this.connection.disconnect(var1.getReason());
	}
	
	@Override
	public void handleKeepAlive(ClientboundKeepAlivePacket var1) {
		//NOOP
	}
	
	@Override
	public void handlePing(ClientboundPingPacket var1) {
		throw0();
	}
	
	@Override
	public void handleAddEntity(ClientboundAddEntityPacket var1) {
		throw0();
	}
	
	@Override
	public void handleAddExperienceOrb(ClientboundAddExperienceOrbPacket var1) {
		throw0();
	}
	
	@Override
	public void handleAddVibrationSignal(ClientboundAddVibrationSignalPacket var1) {
		throw0();
	}
	
	@Override
	public void handleAddMob(ClientboundAddMobPacket var1) {
		throw0();
	}
	
	@Override
	public void handleAddObjective(ClientboundSetObjectivePacket var1) {
		throw0();
	}
	
	@Override
	public void handleAddPainting(ClientboundAddPaintingPacket var1) {
		throw0();
	}
	
	@Override
	public void handleAddPlayer(ClientboundAddPlayerPacket var1) {
		throw0();
	}
	
	@Override
	public void handleAnimate(ClientboundAnimatePacket var1) {
		throw0();
	}
	
	@Override
	public void handleAwardStats(ClientboundAwardStatsPacket var1) {
		throw0();
	}
	
	@Override
	public void handleAddOrRemoveRecipes(ClientboundRecipePacket var1) {
		throw0();
	}
	
	@Override
	public void handleBlockDestruction(ClientboundBlockDestructionPacket var1) {
		throw0();
	}
	
	@Override
	public void handleOpenSignEditor(ClientboundOpenSignEditorPacket var1) {
		throw0();
	}
	
	@Override
	public void handleBlockEntityData(ClientboundBlockEntityDataPacket var1) {
		throw0();
	}
	
	@Override
	public void handleBlockEvent(ClientboundBlockEventPacket var1) {
		throw0();
	}
	
	@Override
	public void handleBlockUpdate(ClientboundBlockUpdatePacket var1) {
		throw0();
	}
	
	@Override
	public void handleChat(ClientboundChatPacket var1) {
		throw0();
	}
	
	@Override
	public void handleChunkBlocksUpdate(ClientboundSectionBlocksUpdatePacket var1) {
		throw0();
	}
	
	@Override
	public void handleMapItemData(ClientboundMapItemDataPacket var1) {
		throw0();
	}
	
	@Override
	public void handleContainerClose(ClientboundContainerClosePacket var1) {
		throw0();
	}
	
	@Override
	public void handleContainerContent(ClientboundContainerSetContentPacket var1) {
		throw0();
	}
	
	@Override
	public void handleHorseScreenOpen(ClientboundHorseScreenOpenPacket var1) {
		throw0();
	}
	
	@Override
	public void handleContainerSetData(ClientboundContainerSetDataPacket var1) {
		throw0();
	}
	
	@Override
	public void handleContainerSetSlot(ClientboundContainerSetSlotPacket var1) {
		throw0();
	}
	
	@Override
	public void handleEntityEvent(ClientboundEntityEventPacket var1) {
		throw0();
	}
	
	@Override
	public void handleEntityLinkPacket(ClientboundSetEntityLinkPacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetEntityPassengersPacket(ClientboundSetPassengersPacket var1) {
		throw0();
	}
	
	@Override
	public void handleExplosion(ClientboundExplodePacket var1) {
		throw0();
	}
	
	@Override
	public void handleGameEvent(ClientboundGameEventPacket var1) {
		throw0();
	}
	
	@Override
	public void handleLevelChunkWithLight(ClientboundLevelChunkWithLightPacket var1) {
		throw0();
	}
	
	@Override
	public void handleForgetLevelChunk(ClientboundForgetLevelChunkPacket var1) {
		throw0();
	}
	
	@Override
	public void handleLevelEvent(ClientboundLevelEventPacket var1) {
		throw0();
	}
	
	@Override
	public void handleLogin(ClientboundLoginPacket var1) {
		throw0();
	}
	
	@Override
	public void handleMoveEntity(ClientboundMoveEntityPacket var1) {
		throw0();
	}
	
	@Override
	public void handleMovePlayer(ClientboundPlayerPositionPacket var1) {
		throw0();
	}
	
	@Override
	public void handleParticleEvent(ClientboundLevelParticlesPacket var1) {
		throw0();
	}
	
	@Override
	public void handlePlayerAbilities(ClientboundPlayerAbilitiesPacket var1) {
		throw0();
	}
	
	@Override
	public void handlePlayerInfo(ClientboundPlayerInfoPacket var1) {
		throw0();
	}
	
	@Override
	public void handleRemoveEntities(ClientboundRemoveEntitiesPacket var1) {
		throw0();
	}
	
	@Override
	public void handleRemoveMobEffect(ClientboundRemoveMobEffectPacket var1) {
		throw0();
	}
	
	@Override
	public void handleRespawn(ClientboundRespawnPacket var1) {
		throw0();
	}
	
	@Override
	public void handleRotateMob(ClientboundRotateHeadPacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetCarriedItem(ClientboundSetCarriedItemPacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetDisplayObjective(ClientboundSetDisplayObjectivePacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetEntityData(ClientboundSetEntityDataPacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetEntityMotion(ClientboundSetEntityMotionPacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetEquipment(ClientboundSetEquipmentPacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetExperience(ClientboundSetExperiencePacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetHealth(ClientboundSetHealthPacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetPlayerTeamPacket(ClientboundSetPlayerTeamPacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetScore(ClientboundSetScorePacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetSpawn(ClientboundSetDefaultSpawnPositionPacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetTime(ClientboundSetTimePacket var1) {
		throw0();
	}
	
	@Override
	public void handleSoundEvent(ClientboundSoundPacket var1) {
		throw0();
	}
	
	@Override
	public void handleSoundEntityEvent(ClientboundSoundEntityPacket var1) {
		throw0();
	}
	
	@Override
	public void handleCustomSoundEvent(ClientboundCustomSoundPacket var1) {
		throw0();
	}
	
	@Override
	public void handleTakeItemEntity(ClientboundTakeItemEntityPacket var1) {
		throw0();
	}
	
	@Override
	public void handleTeleportEntity(ClientboundTeleportEntityPacket var1) {
		throw0();
	}
	
	@Override
	public void handleUpdateAttributes(ClientboundUpdateAttributesPacket var1) {
		throw0();
	}
	
	@Override
	public void handleUpdateMobEffect(ClientboundUpdateMobEffectPacket var1) {
		throw0();
	}
	
	@Override
	public void handleUpdateTags(ClientboundUpdateTagsPacket var1) {
		throw0();
	}
	
	@Override
	public void handlePlayerCombatEnd(ClientboundPlayerCombatEndPacket var1) {
		throw0();
	}
	
	@Override
	public void handlePlayerCombatEnter(ClientboundPlayerCombatEnterPacket var1) {
		throw0();
	}
	
	@Override
	public void handlePlayerCombatKill(ClientboundPlayerCombatKillPacket var1) {
		throw0();
	}
	
	@Override
	public void handleChangeDifficulty(ClientboundChangeDifficultyPacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetCamera(ClientboundSetCameraPacket var1) {
		throw0();
	}
	
	@Override
	public void handleInitializeBorder(ClientboundInitializeBorderPacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetBorderLerpSize(ClientboundSetBorderLerpSizePacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetBorderSize(ClientboundSetBorderSizePacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetBorderWarningDelay(ClientboundSetBorderWarningDelayPacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetBorderWarningDistance(ClientboundSetBorderWarningDistancePacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetBorderCenter(ClientboundSetBorderCenterPacket var1) {
		throw0();
	}
	
	@Override
	public void handleTabListCustomisation(ClientboundTabListPacket var1) {
		throw0();
	}
	
	@Override
	public void handleResourcePack(ClientboundResourcePackPacket var1) {
		throw0();
	}
	
	@Override
	public void handleBossUpdate(ClientboundBossEventPacket var1) {
		throw0();
	}
	
	@Override
	public void handleItemCooldown(ClientboundCooldownPacket var1) {
		throw0();
	}
	
	@Override
	public void handleMoveVehicle(ClientboundMoveVehiclePacket var1) {
		throw0();
	}
	
	@Override
	public void handleUpdateAdvancementsPacket(ClientboundUpdateAdvancementsPacket var1) {
		throw0();
	}
	
	@Override
	public void handleSelectAdvancementsTab(ClientboundSelectAdvancementsTabPacket var1) {
		throw0();
	}
	
	@Override
	public void handlePlaceRecipe(ClientboundPlaceGhostRecipePacket var1) {
		throw0();
	}
	
	@Override
	public void handleCommands(ClientboundCommandsPacket var1) {
		throw0();
	}
	
	@Override
	public void handleStopSoundEvent(ClientboundStopSoundPacket var1) {
		throw0();
	}
	
	@Override
	public void handleCommandSuggestions(ClientboundCommandSuggestionsPacket var1) {
		throw0();
	}
	
	@Override
	public void handleUpdateRecipes(ClientboundUpdateRecipesPacket var1) {
		throw0();
	}
	
	@Override
	public void handleLookAt(ClientboundPlayerLookAtPacket var1) {
		throw0();
	}
	
	@Override
	public void handleTagQueryPacket(ClientboundTagQueryPacket var1) {
		throw0();
	}
	
	@Override
	public void handleLightUpdatePacket(ClientboundLightUpdatePacket var1) {
		throw0();
	}
	
	@Override
	public void handleOpenBook(ClientboundOpenBookPacket var1) {
		throw0();
	}
	
	@Override
	public void handleOpenScreen(ClientboundOpenScreenPacket var1) {
		throw0();
	}
	
	@Override
	public void handleMerchantOffers(ClientboundMerchantOffersPacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetChunkCacheRadius(ClientboundSetChunkCacheRadiusPacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetSimulationDistance(ClientboundSetSimulationDistancePacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetChunkCacheCenter(ClientboundSetChunkCacheCenterPacket var1) {
		throw0();
	}
	
	@Override
	public void handleBlockBreakAck(ClientboundBlockBreakAckPacket var1) {
		throw0();
	}
	
	@Override
	public void setActionBarText(ClientboundSetActionBarTextPacket var1) {
		throw0();
	}
	
	@Override
	public void setSubtitleText(ClientboundSetSubtitleTextPacket var1) {
		throw0();
	}
	
	@Override
	public void setTitleText(ClientboundSetTitleTextPacket var1) {
		throw0();
	}
	
	@Override
	public void setTitlesAnimation(ClientboundSetTitlesAnimationPacket var1) {
		throw0();
	}
	
	@Override
	public void handleTitlesClear(ClientboundClearTitlesPacket var1) {
		throw0();
	}
}
