package net.wolftail.impl.tracker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.wolftail.impl.tracker.ExtTrackerChunk;

//prevent unload chunk
@Mixin(ChunkProviderServer.class)
public abstract class MixinChunkProviderServer {
	
	@Inject(method = "queueUnload", at = @At("HEAD"), cancellable = true)
	private void on_queueUnload_head(Chunk chunkIn, CallbackInfo ci) {
		if(((ExtTrackerChunk) chunkIn).wolftail_preventUnload())
			ci.cancel();
	}
}
