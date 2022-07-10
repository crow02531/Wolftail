package net.wolftail.impl.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.server.SPacketLoginSuccess;
import net.wolftail.impl.ExtensionsNetHandlerLoginClient;

@Mixin(NetHandlerLoginClient.class)
public abstract class MixinNetHandlerLoginClient implements ExtensionsNetHandlerLoginClient {
	
	@Final
	@Shadow
	private NetworkManager networkManager;
	
	@Unique
	private SPacketLoginSuccess stored_ls_packet;
	
	@Inject(method = "handleLoginSuccess", at = @At("HEAD"), cancellable = true)
	private void onHandleLoginSuccess(SPacketLoginSuccess packetIn, CallbackInfo info) {
		if(this.stored_ls_packet == null) {
			info.cancel();
			
			//when receiving SPacketLoginSuccess, client will store it and wait
			//for SPacketTypeNotify
			
			this.stored_ls_packet = packetIn;
			this.networkManager.setConnectionState(EnumConnectionState.PLAY); //set it so that WPS enabled
		}
	}
	
	@Override
	public SPacketLoginSuccess wolftail_getStoredLoginSuccessPacket() {
		return this.stored_ls_packet;
	}
	
	@Override
	public NetworkManager wolftail_getConnection() {
		return this.networkManager;
	}
	
	@Override
	public void wolftail_npt_clearStoredPacketRef() {
		this.stored_ls_packet = null;
	}
}
