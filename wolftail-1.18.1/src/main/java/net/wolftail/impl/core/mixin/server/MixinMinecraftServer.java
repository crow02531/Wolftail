package net.wolftail.impl.core.mixin.server;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.server.MinecraftServer;
import net.wolftail.api.lifecycle.PhysicalType;
import net.wolftail.impl.core.SectionHandler;

//SH server side: finish_loading, capture dedicatedServerRegularThread
@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {
	
	@SuppressWarnings("rawtypes")
	@Inject(method = "spin", at = @At(value = "INVOKE", target = "start()V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void on_spin_invoke_start(Function function, CallbackInfoReturnable<MinecraftServer> cir, AtomicReference atomicReference, Thread thread, MinecraftServer minecraftServer) {
		if(PhysicalType.DEDICATED_SERVER.is()) {
			SectionHandler.dedicatedServerRegularThread = thread; //set before the thread starts
			SectionHandler.finish_loading(true);
		}
	}
}
