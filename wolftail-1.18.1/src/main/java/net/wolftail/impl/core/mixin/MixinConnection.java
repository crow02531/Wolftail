package net.wolftail.impl.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.wolftail.impl.core.network.NptPacketListener;

//tick NptPacketListener
@Mixin(Connection.class)
public abstract class MixinConnection {
	
	@Shadow
	public PacketListener packetListener;
	
	@Inject(method = "tick", at = @At(value = "INVOKE", target = "flushQueue()V", shift = Shift.AFTER))
	private void on_tick_invokeAfter_flushQueue(CallbackInfo ci) {
		if(this.packetListener instanceof NptPacketListener l)
			l.tick();
	}
}
