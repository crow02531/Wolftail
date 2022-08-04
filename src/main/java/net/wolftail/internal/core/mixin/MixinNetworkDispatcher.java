package net.wolftail.internal.core.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.internal.core.ExtCoreMinecraftServer;
import net.wolftail.internal.core.ExtCorePlayerList;
import net.wolftail.internal.core.ImplPCS;
import net.wolftail.internal.core.ImplUPT;
import net.wolftail.internal.core.network.NptPacketListener;
import net.wolftail.internal.core.network.NptServerPacketListener;

//server side accepts uniplayers
@Mixin(NetworkDispatcher.class)
public abstract class MixinNetworkDispatcher {
	
	@Shadow(remap = false)
	public EntityPlayerMP player;
	
	@Final
	@Shadow(remap = false)
	public NetworkManager manager;
	
	@Inject(method = "completeServerSideConnection", at = @At(value = "INVOKE", target = "net.minecraft.server.management.PlayerList.initializeConnectionToPlayer(Lnet/minecraft/network/NetworkManager;Lnet/minecraft/entity/player/EntityPlayerMP;Lnet/minecraft/network/NetHandlerPlayServer;)V", remap = false), cancellable = true, remap = false)
	private void on_completeServerSideConnection_invoke_initializeConnectionToPlayer(CallbackInfo ci) {
		GameProfile profile = this.player.getGameProfile();
		NetworkManager connect = this.manager;
		
		// we should in LOGIC_SERVER thread, modded connection
		
		ImplPCS context = ((ExtCoreMinecraftServer) GameSection.serverInstance()).wolftail_getRootManager()
				.login(connect, profile.getId(), profile.getName());
		ImplUPT type = context.playType();
		
		connect.sendPacket(newTypeNotifyPacket(type));
		
		if (!type.isPlayerType()) {
			ci.cancel();
			
			// clean up
			NptPacketListener.cleanFML(connect);
			this.cleanOthers();
			connect.setNetHandler(new NptServerPacketListener(context));
			
			type.callServerEnter(context);
		}
	}
	
	@Unique
	private static SPacketCustomPayload newTypeNotifyPacket(ImplUPT type) {
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
		buf.writeResourceLocation(type.registeringId());
		
		return new SPacketCustomPayload("WT|TN", buf);
	}
	
	@Unique
	private void cleanOthers() {
		ExtCorePlayerList ext = (ExtCorePlayerList) GameSection.serverInstance().getPlayerList();
		
		ext.wolftail_rem_advancements(this.player.getUniqueID());
		ext.wolftail_rem_playerStatFiles(this.player.getUniqueID());
	}
}
