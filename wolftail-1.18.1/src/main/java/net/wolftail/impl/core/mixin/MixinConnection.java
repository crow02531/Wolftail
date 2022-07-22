package net.wolftail.impl.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.Connection;
import net.wolftail.impl.core.ExtCoreConnection;
import net.wolftail.impl.core.ImplPC;
import net.wolftail.impl.core.ImplPCS;

@Mixin(Connection.class)
public abstract class MixinConnection implements ExtCoreConnection {
	
	@Unique
	private ImplPC playContext;
	
	@Inject(method = "tick", at = @At(value = "INVOKE", target = "flushQueue()V", shift = Shift.AFTER))
	private void on_tick_invokeAfter_flushQueue(CallbackInfo ci) {
		
	}
	
	@Inject(method = "handleDisconnection", at = @At(value = "INVOKE", target = "onDisconnect(Lnet/minecraft/network/chat/Component;)V", shift = Shift.AFTER))
	private void on_handleDisconnection_invokeAfter_onDisconnect(CallbackInfo ci) {
		if(this.playContext instanceof ImplPCS pcs)
			pcs.subManager().rootManager().logout(pcs);
	}
	
	@Override
	public void wolftail_setPlayContext(ImplPC context) {
		this.playContext = context;
	}
}
