package net.wolftail.impl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.wolftail.impl.SharedImpls;

//ContentTracker Supporter, prevent unloading subscribed chunk
@Mixin(ChunkProviderServer.class)
public abstract class MixinChunkProviderServer {
	
	@Inject(method = "queueUnload", at = @At("HEAD"), cancellable = true)
	private void onQueueUnload(Chunk chunkIn, CallbackInfo info) {
		if(SharedImpls.as(chunkIn).wolftail_hasSubscriber())
			info.cancel();
	}
}
