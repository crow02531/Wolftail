package net.wolftail.impl.core.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.wolftail.impl.core.ExtCoreNetworkManager;
import net.wolftail.impl.core.ImplPC;

//server side steve disconnect
@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer {
	
	@Final
	@Shadow
	public NetworkManager netManager;
	
	@Inject(method = "onDisconnect", at = @At(value = "INVOKE", target = "playerLoggedOut(Lnet/minecraft/entity/player/EntityPlayerMP;)V", shift = Shift.AFTER))
	private void onOnDisconnect(CallbackInfo info) {
		ImplPC.Server context = (ImplPC.Server) ((ExtCoreNetworkManager) this.netManager).wolftail_getPlayContext();
		
		context.manager().rootManager().logout(context);
	}
}
