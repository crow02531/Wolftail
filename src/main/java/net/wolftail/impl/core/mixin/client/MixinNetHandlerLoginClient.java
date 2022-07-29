package net.wolftail.impl.core.mixin.client;

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
import net.wolftail.impl.core.network.TransientPacketListener;

//intercept login success packet
@Mixin(NetHandlerLoginClient.class)
public abstract class MixinNetHandlerLoginClient {
	
	@Final
	@Shadow
	public NetworkManager networkManager;
	
	@Unique
	private boolean waitForTypeNotify;
	
	@Inject(method = "handleLoginSuccess", at = @At("HEAD"), cancellable = true)
	private void on_handleLoginSuccess_head(SPacketLoginSuccess packetIn, CallbackInfo ci) {
		if (!this.waitForTypeNotify) {
			ci.cancel();
			
			NetworkManager conn = this.networkManager;
			
			conn.setConnectionState(EnumConnectionState.PLAY);
			conn.setNetHandler(new TransientPacketListener(conn, (NetHandlerLoginClient) (Object) this, packetIn));
			
			this.waitForTypeNotify = true;
		}
	}
}
