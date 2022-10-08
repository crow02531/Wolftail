package net.wolftail.internal.core.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketEnchantItem;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlaceRecipe;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketRecipeInfo;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.client.CPacketSeenAdvancements;
import net.minecraft.network.play.client.CPacketSpectate;
import net.minecraft.network.play.client.CPacketSteerBoat;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.wolftail.internal.core.ImplMPCR;
import net.wolftail.internal.core.ImplPCS;

public final class NptServerPacketListener extends NptPacketListener implements INetHandlerPlayServer, ITickable {

	private static final Logger logger0 = LogManager.getLogger("Wolftail/User");
	private static final Logger logger1 = LogManager.getLogger(NetHandlerPlayServer.class);

	private final ImplPCS context;

	private long keepAliveTimer;

	public NptServerPacketListener(ImplPCS context) {
		this.context = context;
	}

	@Override
	public void update() {
		if (!this.context.getConnection().isLocalChannel()) {
			long ct = System.currentTimeMillis();

			if (ct > this.keepAliveTimer + 15000L) {
				this.keepAliveTimer = ct;

				this.context.getConnection().sendPacket(new SPacketKeepAlive(0));
			}
		}
	}

	@Override
	public void onDisconnect(ITextComponent reason) {
		ImplPCS pc = this.context;
		ImplMPCR rm = pc.subManager().rootManager();

		logger0.info("{} lost connection: {}", pc.playName(), reason.getUnformattedText());

		pc.playType().callServerLeave(pc);
		rm.logout(pc);

		if (pc.getConnection().isLocalChannel()) {
			logger1.info("Stopping singleplayer server as player logged out");

			rm.server().initiateShutdown();
		}
	}

	@Override
	public void processCustomPayload(CPacketCustomPayload packetIn) {
		try {
			check0(packetIn.getChannelName());

			this.handlePayload(packetIn.getBufferData());
		} catch (Throwable e) {
			packetIn.getBufferData().release();

			throw e;
		}
	}

	@Override
	public void processKeepAlive(CPacketKeepAlive packetIn) {
		// NOOP
	}

	@Override
	public void handleAnimation(CPacketAnimation packetIn) {
		throw0();
	}

	@Override
	public void processChatMessage(CPacketChatMessage packetIn) {
		throw0();
	}

	@Override
	public void processTabComplete(CPacketTabComplete packetIn) {
		throw0();
	}

	@Override
	public void processClientStatus(CPacketClientStatus packetIn) {
		throw0();
	}

	@Override
	public void processClientSettings(CPacketClientSettings packetIn) {
		throw0();
	}

	@Override
	public void processConfirmTransaction(CPacketConfirmTransaction packetIn) {
		throw0();
	}

	@Override
	public void processEnchantItem(CPacketEnchantItem packetIn) {
		throw0();
	}

	@Override
	public void processClickWindow(CPacketClickWindow packetIn) {
		throw0();
	}

	@Override
	public void func_194308_a(CPacketPlaceRecipe p_194308_1_) {
		throw0();
	}

	@Override
	public void processCloseWindow(CPacketCloseWindow packetIn) {
		throw0();
	}

	@Override
	public void processUseEntity(CPacketUseEntity packetIn) {
		throw0();
	}

	@Override
	public void processPlayer(CPacketPlayer packetIn) {
		throw0();
	}

	@Override
	public void processPlayerAbilities(CPacketPlayerAbilities packetIn) {
		throw0();
	}

	@Override
	public void processPlayerDigging(CPacketPlayerDigging packetIn) {
		throw0();
	}

	@Override
	public void processEntityAction(CPacketEntityAction packetIn) {
		throw0();
	}

	@Override
	public void processInput(CPacketInput packetIn) {
		throw0();
	}

	@Override
	public void processHeldItemChange(CPacketHeldItemChange packetIn) {
		throw0();
	}

	@Override
	public void processCreativeInventoryAction(CPacketCreativeInventoryAction packetIn) {
		throw0();
	}

	@Override
	public void processUpdateSign(CPacketUpdateSign packetIn) {
		throw0();
	}

	@Override
	public void processTryUseItemOnBlock(CPacketPlayerTryUseItemOnBlock packetIn) {
		throw0();
	}

	@Override
	public void processTryUseItem(CPacketPlayerTryUseItem packetIn) {
		throw0();
	}

	@Override
	public void handleSpectate(CPacketSpectate packetIn) {
		throw0();
	}

	@Override
	public void handleResourcePackStatus(CPacketResourcePackStatus packetIn) {
		throw0();
	}

	@Override
	public void processSteerBoat(CPacketSteerBoat packetIn) {
		throw0();
	}

	@Override
	public void processVehicleMove(CPacketVehicleMove packetIn) {
		throw0();
	}

	@Override
	public void processConfirmTeleport(CPacketConfirmTeleport packetIn) {
		throw0();
	}

	@Override
	public void handleRecipeBookUpdate(CPacketRecipeInfo p_191984_1_) {
		throw0();
	}

	@Override
	public void handleSeenAdvancements(CPacketSeenAdvancements p_194027_1_) {
		throw0();
	}
}
