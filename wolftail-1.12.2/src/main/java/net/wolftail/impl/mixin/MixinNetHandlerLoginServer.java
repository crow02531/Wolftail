package net.wolftail.impl.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.NetHandlerLoginServer;
import net.wolftail.api.UniversalPlayerType;
import net.wolftail.impl.ImplPCServer;
import net.wolftail.impl.ImplUPT;
import net.wolftail.impl.SharedImpls;
import net.wolftail.impl.network.DefaultNetHandler;
import net.wolftail.impl.network.S2CPacketTypeNotify;

//accept universal player
@Mixin(NetHandlerLoginServer.class)
public abstract class MixinNetHandlerLoginServer {
	
	@Final
	@Shadow
	private MinecraftServer server;
	
	@Final
	@Shadow
    public NetworkManager networkManager;
	
	@Shadow
	private GameProfile loginGameProfile;
	
	@Inject(method = "tryAcceptPlayer", at = @At(value = "INVOKE", target = "sendPacket(Lnet/minecraft/network/Packet;)V", shift = Shift.AFTER), cancellable = true)
	private void onTryAcceptPlayer(CallbackInfo info) throws InterruptedException {
		GameProfile profile = this.loginGameProfile;
		NetworkManager connect = this.networkManager;
		
		//now it was EnumConnectionState.LOGIN state and vanilla connection has just set up
		//we should in LOGIC_SERVER thread
		
		ImplPCServer context = SharedImpls.as(this.server).wolftail_getRootManager().join(connect, profile.getId(), profile.getName());
		ImplUPT type = context.playType();
		
		SharedImpls.as(connect).wolftail_setPlayContext(context);
		
		//the connection state setting action will be executed in netty's event loop thread, it will be set to PLAY
		connect.sendPacket(new S2CPacketTypeNotify(type));
		
		//wolftail connection set up
		
		SharedImpls.LOGGER_NETWORK.info("Server side wolftail connection set up, with remote address {}", connect.getRemoteAddress());
		SharedImpls.LOGGER_USER.info("{}({}) the universal player logged in with type {} and address {}", profile.getId(), profile.getName(), type.registeringId(), connect.getRemoteAddress());
		
		if(type != UniversalPlayerType.TYPE_PLAYER) {
			info.cancel();
			
			connect.setNetHandler(new DefaultNetHandler());
			type.callServerEnter(context);
		}
	}
}
