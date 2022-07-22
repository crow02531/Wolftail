package net.wolftail.impl.core.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.Main;
import net.wolftail.impl.core.SectionHandler;

//SH server side: finish_preparing
@Mixin(Main.class)
public abstract class MixinMain {
	
	@Inject(method = "main", at = @At("HEAD"))
	private static void on_main_head(CallbackInfo ci) {
		SectionHandler.finish_preparing();
	}
}
