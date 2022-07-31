package net.wolftail.internal.tracker.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.wolftail.internal.tracker.Mechanisms;

//run assembling mechanisms
@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {
	
	@Inject(method = "tick", at = @At(value = "FIELD", target = "tickCounter:I", opcode = Opcodes.PUTFIELD))
	private void on_tick_putField_tickCounter(CallbackInfo ci) {
		Mechanisms.run();
	}
}
