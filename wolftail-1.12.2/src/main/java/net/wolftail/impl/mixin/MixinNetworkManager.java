package net.wolftail.impl.mixin;

import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.wolftail.api.UniversalPlayerType;
import net.wolftail.impl.ExtensionsNetworkManager;
import net.wolftail.impl.ImplPC;
import net.wolftail.impl.ImplPCServer;
import net.wolftail.impl.SharedImpls;
import net.wolftail.impl.network.BPacketNPTKeepAlive;

//WPS supporter, check (S) connection disconnect, keep alive supporter
@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager implements ExtensionsNetworkManager {
	
	@Final
	@Shadow
	private static Logger LOGGER;
	
	@Final
	@Shadow
	private EnumPacketDirection direction; //you will found that this isn't the 'packet direction' but the side where the connection instance is
	
	@Shadow
	private INetHandler packetListener;
	
	@Shadow
	public abstract boolean isLocalChannel();
	
	@Shadow
	public abstract void sendPacket(Packet<?> packetIn);
	
	@Unique
	private ImplPC play_context;
	
	@Redirect(method = "dispatchPacket", at = @At(value = "INVOKE", target = "getFromPacket(Lnet/minecraft/network/Packet;)Lnet/minecraft/network/EnumConnectionState;"))
	private EnumConnectionState proxyGetFromPacket(Packet<?> inPacket) {
		EnumConnectionState ret = EnumConnectionState.getFromPacket(inPacket);
		
		return ret == null ? EnumConnectionState.PLAY : ret;
	}
	
	@Inject(method = "checkDisconnected", at = @At(value = "INVOKE", target = "onDisconnect(Lnet/minecraft/util/text/ITextComponent;)V", shift = Shift.AFTER))
	private void onCheckDisconnected(CallbackInfo info) {
		//these codes would run in the Main Thread, both LOGIC_CLIENT and LOGIC_SERVER
		
		if(this.direction == EnumPacketDirection.SERVERBOUND) {
			//server side logic
			
			ImplPCServer context = (ImplPCServer) this.play_context;
			if(context == null) return;
			
			if(context.playType() != UniversalPlayerType.TYPE_PLAYERS) {
				SharedImpls.shared_func_disconnect(context);
				
				if(this.isLocalChannel()) {
					LOGGER.info("Stopping singleplayer server as player logged out");
					
					context.manager().rootManager().server().initiateShutdown();
				}
			} else if(!(this.packetListener instanceof INetHandlerPlayServer))
				SharedImpls.shared_func_disconnect(context); //sometimes you will found connection close without a NetHandlerPlayServer, i.e. in NetHandlerLoginServer
		}
	}
	
	@Inject(method = "processReceivedPackets", at = @At(value = "INVOKE", target = "flushOutboundQueue()V", shift = Shift.AFTER))
	private void onProcessReceivedPackets(CallbackInfo info) {
		if(this.isLocalChannel()) return; //local channel dosen't need keep alive
		
		ImplPC context = this.play_context;
		
		if(context != null && context.playType() != UniversalPlayerType.TYPE_PLAYERS) {
			if(context.keepAlive_receivedPkt != null) {
				long t = context.keepAlive_timer;
				
				if(t == 0) {
					context.keepAlive_timer = System.currentTimeMillis();
				} else if(System.currentTimeMillis() - t > 15000L) {
					this.sendPacket(context.keepAlive_receivedPkt);
					
					context.keepAlive_receivedPkt = null;
					context.keepAlive_timer = 0;
				}
			}
		}
	}
	
	@Inject(method = "channelRead0", at = @At(value = "INVOKE", target = "processPacket(Lnet/minecraft/network/INetHandler;)V"), cancellable = true)
	private void onChannelRead0(ChannelHandlerContext ctx, Packet<?> pkt, CallbackInfo info) throws IOException {
		//we are in the netty's thread
		
		if(pkt instanceof BPacketNPTKeepAlive) {
			info.cancel();
			
			ImplPC context = this.play_context;
			
			if(context.keepAlive_receivedPkt == null && context.playType() != UniversalPlayerType.TYPE_PLAYERS) {
				context.keepAlive_receivedPkt = pkt;
			} else throw new IOException("Unexpected keep alive packet");
		}
	}
	
	@Override
	public void wolftail_setPlayContext(ImplPC context) {
		this.play_context = context;
	}
	
	@Override
	public ImplPC wolftail_getPlayContext() {
		return this.play_context;
	}
}
