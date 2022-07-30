package net.wolftail.internal.tracker.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.wolftail.internal.tracker.Mechanisms;
import net.wolftail.util.tracker.ContentTracker;

//anchor of content tracker
@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer implements ContentTracker {
	
	@Unique
	private boolean assembling;
	
	@Inject(method = "tick", at = @At(value = "FIELD", target = "tickCounter:I", opcode = Opcodes.PUTFIELD))
	private void on_tick_putField_tickCounter(CallbackInfo info) {
		this.assembling = true;
		
		// assemble
		Mechanisms.run();
		
		this.assembling = false;
	}
	
	@Override
	public boolean inAssemble() {
		return this.assembling;
	}
}
