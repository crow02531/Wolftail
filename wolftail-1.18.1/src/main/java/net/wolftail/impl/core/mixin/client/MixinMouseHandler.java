package net.wolftail.impl.core.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.wolftail.impl.core.ExtCoreMinecraft;

@Mixin(MouseHandler.class)
public abstract class MixinMouseHandler {
	
	@Redirect(method = "onPress", at = @At(value = "INVOKE", target = "grabMouse()V"))
	private void proxy_onPress_grabMouse(MouseHandler this0) {
		if(!ExtCoreMinecraft.isNptPlaying())
			this0.grabMouse();
	}
	
	@Redirect(method = "onPress", at = @At(value = "INVOKE", target = "isSpectator()Z"))
	private boolean proxy_onPress_isSpectator(LocalPlayer p) {
		return p == null ? false : p.isSpectator();
	}
	
	@Inject(method = "turnPlayer", at = @At(value = "INVOKE", target = "isMouseGrabbed()Z"), cancellable = true)
	private void on_turnPlayer_invoke_isMouseGrabbed(CallbackInfo ci) {
		if(ExtCoreMinecraft.isNptPlaying())
			ci.cancel();
	}
}
