package net.wolftail.impl.core.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.main.Main;
import net.wolftail.impl.core.SectionHandler;

//SH client side: finish_preparing, finish_loading
@Mixin(Main.class)
public abstract class MixinMain {
	
	@Inject(method = "main", at = @At("HEAD"))
	private static void on_main_head(CallbackInfo ci) {
		SectionHandler.finish_preparing();
	}
	
	@Inject(method = "main", at = @At(value = "INVOKE", target = "finishInitialization()V"))
	private static void on_main_invoke_finishInitialization(CallbackInfo ci) {
		SectionHandler.finish_loading(false);
	}
}
