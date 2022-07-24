package net.wolftail.impl.core.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.wolftail.impl.core.ExtCoreMinecraft;

@Mixin(PauseScreen.class)
public abstract class MixinPauseScreen {
	
	@Inject(method = "method_19845", at = @At(value = "INVOKE", target = "setScreen", shift = Shift.AFTER), cancellable = true)
	private void on_method_19845_invokeAfter_setScreen(CallbackInfo ci) {
		ci.cancel();
	}
	
	@Inject(method = "method_19836", at = @At("HEAD"), cancellable = true)
	private void on_method_19836_head(CallbackInfo ci) {
		if(ExtCoreMinecraft.isNptPlaying()) {
			ci.cancel();
			
			((ExtCoreMinecraft) Minecraft.getInstance()).wolftail_getContext().disconnect();
		}
	}
}
