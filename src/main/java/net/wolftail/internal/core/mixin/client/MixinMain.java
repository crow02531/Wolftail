package net.wolftail.internal.core.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.main.Main;
import net.wolftail.internal.core.SectionHandler;

//SH: finish_preparing
@Mixin(Main.class)
public abstract class MixinMain {
	
	@Inject(method = "main", at = @At("HEAD"))
	private static void on_main_head(CallbackInfo info) {
		SectionHandler.finish_preparing();
	}
}
