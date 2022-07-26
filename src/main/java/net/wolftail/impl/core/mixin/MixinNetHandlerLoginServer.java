package net.wolftail.impl.core.mixin;

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
import net.wolftail.impl.core.ExtCoreMinecraftServer;
import net.wolftail.impl.core.ExtCoreNetworkManager;
import net.wolftail.impl.core.ImplPC;
import net.wolftail.impl.core.ImplUPT;
import net.wolftail.impl.core.network.NoopNetHandler;
import net.wolftail.impl.core.network.S2CPacketTypeNotify;

//server side accept uniplayer
@Mixin(NetHandlerLoginServer.class)
public abstract class MixinNetHandlerLoginServer {
	
	@Final
	@Shadow
	public MinecraftServer server;
	
	@Final
	@Shadow
    public NetworkManager networkManager;
	
	@Shadow
	public GameProfile loginGameProfile;
	
	@Inject(method = "tryAcceptPlayer", at = @At(value = "INVOKE", target = "sendPacket(Lnet/minecraft/network/Packet;)V", shift = Shift.AFTER), cancellable = true)
	private void onTryAcceptPlayer(CallbackInfo info) throws InterruptedException {
		GameProfile profile = this.loginGameProfile;
		NetworkManager connect = this.networkManager;
		
		//now it was EnumConnectionState.LOGIN state and vanilla connection has just set up
		//we should in LOGIC_SERVER thread
		
		ImplPC.Server context = ((ExtCoreMinecraftServer) this.server).wolftail_getRootManager().login(connect, profile.getId(), profile.getName());
		ImplUPT type = context.playType();
		
		((ExtCoreNetworkManager) connect).wolftail_setPlayContext(context);
		//the connection state setting action will be executed in netty's event loop thread, it will be set to PLAY
		connect.sendPacket(new S2CPacketTypeNotify(type));
		
		if(type != UniversalPlayerType.TYPE_PLAYER) {
			info.cancel();
			
			connect.setNetHandler(new NoopNetHandler());
			type.callServerEnter(context);
		}
	}
}
