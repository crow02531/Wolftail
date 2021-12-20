package net.wolftail.impl.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.main.Main;
import net.wolftail.api.UniversalPlayerType;
import net.wolftail.impl.SharedImpls;

@Mixin(Main.class)
public abstract class MixinMain {
	
	@Inject(method = "main", at = @At("HEAD"))
	private static void onMain(CallbackInfo info) {
		SharedImpls.Holder1.finish_preparing();
		SharedImpls.clinit(UniversalPlayerType.class);
	}
}
