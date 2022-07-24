package net.wolftail.impl.core.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQuery;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.network.protocol.game.ServerboundEntityTagQuery;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundJigsawGeneratePacket;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemPacket;
import net.minecraft.network.protocol.game.ServerboundPlaceRecipePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundPongPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookChangeSettingsPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookSeenRecipePacket;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.network.protocol.game.ServerboundSetBeaconPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSetCommandBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSetCommandMinecartPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.network.protocol.game.ServerboundSetJigsawBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.wolftail.impl.core.ImplMPCR;
import net.wolftail.impl.core.ImplPCS;

public final class NptServerPacketListener
extends NptPacketListener implements ServerGamePacketListener {
	
	private static final Logger logger0 = LogManager.getLogger("Wolftail/User");
	private static final Logger logger1 = LogManager.getLogger(ServerGamePacketListenerImpl.class);
	
	private final ImplPCS context;
	
	public NptServerPacketListener(ImplPCS context) {
		this.context = context;
	}
	
	@Override
	public void onDisconnect(Component var1) {
		ImplPCS pc = this.context;
		ImplMPCR rm = pc.subManager().rootManager();
		
		logger0.info("{} lost connection: {}", pc.playName(), var1.getString());
		
		pc.playType().callServerLeave(pc);
		rm.logout(pc);
		
		if(pc.getConnection().isMemoryConnection()) {
			logger1.info("Stopping singleplayer server as player logged out");
			
			rm.server().halt(false);
		}
	}
	
	@Override
	public void handleKeepAlive(ServerboundKeepAlivePacket var1) {
		//TODO KEEPALIVE server
	}
	
	@Override
	public void handleCustomPayload(ServerboundCustomPayloadPacket var1) {
		check0(var1.getIdentifier());
		
		this.handlePayload(var1.getData());
	}
	
	@Override
	public void handlePong(ServerboundPongPacket var1) {
		throw0();
	}
	
	@Override
	public void handleAnimate(ServerboundSwingPacket var1) {
		throw0();
	}
	
	@Override
	public void handleChat(ServerboundChatPacket var1) {
		throw0();
	}
	
	@Override
	public void handleClientCommand(ServerboundClientCommandPacket var1) {
		throw0();
	}
	
	@Override
	public void handleClientInformation(ServerboundClientInformationPacket var1) {
		throw0();
	}
	
	@Override
	public void handleContainerButtonClick(ServerboundContainerButtonClickPacket var1) {
		throw0();
	}
	
	@Override
	public void handleContainerClick(ServerboundContainerClickPacket var1) {
		throw0();
	}
	
	@Override
	public void handlePlaceRecipe(ServerboundPlaceRecipePacket var1) {
		throw0();
	}
	
	@Override
	public void handleContainerClose(ServerboundContainerClosePacket var1) {
		throw0();
	}
	
	@Override
	public void handleInteract(ServerboundInteractPacket var1) {
		throw0();
	}
	
	@Override
	public void handleMovePlayer(ServerboundMovePlayerPacket var1) {
		throw0();
	}
	
	@Override
	public void handlePlayerAbilities(ServerboundPlayerAbilitiesPacket var1) {
		throw0();
	}
	
	@Override
	public void handlePlayerAction(ServerboundPlayerActionPacket var1) {
		throw0();
	}
	
	@Override
	public void handlePlayerCommand(ServerboundPlayerCommandPacket var1) {
		throw0();
	}
	
	@Override
	public void handlePlayerInput(ServerboundPlayerInputPacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetCarriedItem(ServerboundSetCarriedItemPacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket var1) {
		throw0();
	}
	
	@Override
	public void handleSignUpdate(ServerboundSignUpdatePacket var1) {
		throw0();
	}
	
	@Override
	public void handleUseItemOn(ServerboundUseItemOnPacket var1) {
		throw0();
	}
	
	@Override
	public void handleUseItem(ServerboundUseItemPacket var1) {
		throw0();
	}
	
	@Override
	public void handleTeleportToEntityPacket(ServerboundTeleportToEntityPacket var1) {
		throw0();
	}
	
	@Override
	public void handleResourcePackResponse(ServerboundResourcePackPacket var1) {
		throw0();
	}
	
	@Override
	public void handlePaddleBoat(ServerboundPaddleBoatPacket var1) {
		throw0();
	}
	
	@Override
	public void handleMoveVehicle(ServerboundMoveVehiclePacket var1) {
		throw0();
	}
	
	@Override
	public void handleAcceptTeleportPacket(ServerboundAcceptTeleportationPacket var1) {
		throw0();
	}
	
	@Override
	public void handleRecipeBookSeenRecipePacket(ServerboundRecipeBookSeenRecipePacket var1) {
		throw0();
	}
	
	@Override
	public void handleRecipeBookChangeSettingsPacket(ServerboundRecipeBookChangeSettingsPacket var1) {
		throw0();
	}
	
	@Override
	public void handleSeenAdvancements(ServerboundSeenAdvancementsPacket var1) {
		throw0();
	}
	
	@Override
	public void handleCustomCommandSuggestions(ServerboundCommandSuggestionPacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetCommandBlock(ServerboundSetCommandBlockPacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetCommandMinecart(ServerboundSetCommandMinecartPacket var1) {
		throw0();
	}
	
	@Override
	public void handlePickItem(ServerboundPickItemPacket var1) {
		throw0();
	}
	
	@Override
	public void handleRenameItem(ServerboundRenameItemPacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetBeaconPacket(ServerboundSetBeaconPacket var1) {
		throw0();
	}
	
	@Override
	public void handleSetStructureBlock(ServerboundSetStructureBlockPacket var1) {
		throw0();
	}
	
	@Override
	public void handleSelectTrade(ServerboundSelectTradePacket var1) {
		throw0();
	}
	
	@Override
	public void handleEditBook(ServerboundEditBookPacket var1) {
		throw0();
	}
	
	@Override
	public void handleEntityTagQuery(ServerboundEntityTagQuery var1) {
		throw0();
	}
	
	@Override
	public void handleBlockEntityTagQuery(ServerboundBlockEntityTagQuery var1) {
		throw0();
	}
	
	@Override
	public void handleSetJigsawBlock(ServerboundSetJigsawBlockPacket var1) {
		throw0();
	}
	
	@Override
	public void handleJigsawGenerate(ServerboundJigsawGeneratePacket var1) {
		throw0();
	}
	
	@Override
	public void handleChangeDifficulty(ServerboundChangeDifficultyPacket var1) {
		throw0();
	}
	
	@Override
	public void handleLockDifficulty(ServerboundLockDifficultyPacket var1) {
		throw0();
	}
}
