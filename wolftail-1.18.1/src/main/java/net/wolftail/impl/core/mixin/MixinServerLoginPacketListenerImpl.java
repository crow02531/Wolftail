package net.wolftail.impl.core.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import io.netty.buffer.Unpooled;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.wolftail.impl.core.ExtCoreConnection;
import net.wolftail.impl.core.ExtCoreMinecraftServer;
import net.wolftail.impl.core.ImplPCS;
import net.wolftail.impl.core.ImplUPT;
import net.wolftail.impl.core.network.Constants;

//accept uniplayer in server side
@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class MixinServerLoginPacketListenerImpl {
	
	@Final
	@Shadow
	public MinecraftServer server;
	
	@Final
	@Shadow
	public Connection connection;
	
	@Shadow
	public GameProfile gameProfile;
	
	@Inject(method = "handleAcceptedLogin", at = @At(value = "INVOKE", target = "sendPacket(Lnet/minecraft/network/protocol/Packet;)V"), cancellable = true)
	private void on_handleAcceptedLogin_invoke_send_1(CallbackInfo ci) {
		GameProfile profile = this.gameProfile;
		Connection connect = this.connection;
		
		ImplPCS context = ((ExtCoreMinecraftServer) this.server).wolftail_getRootManager().login(connect, profile.getId(), profile.getName());
		ImplUPT type = context.playType();
		
		((ExtCoreConnection) connect).wolftail_setPlayContext(context);
		connect.send(newTypeNotifyPacket(type));
		
		if(!type.isPlayerType()) {
			ci.cancel();
			
			connect.send(new ClientboundGameProfilePacket(profile));
			connect.setProtocol(ConnectionProtocol.PLAY);
			connect.setListener(null);
			
			type.callServerEnter(context);
		}
	}
	
	@Unique
	private static ClientboundCustomQueryPacket newTypeNotifyPacket(ImplUPT type) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeResourceLocation(type.registeringId());
		
		return new ClientboundCustomQueryPacket(0, Constants.CHANNEL_TYPE_NOTIFY, buf);
	}
}
