package net.wolftail.internal.core.mixin;

import java.io.IOException;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.wolftail.internal.core.ExtCoreMinecraftServer;
import net.wolftail.internal.core.ImplMPCR;

//root manager: add root manager, loadDat, saveDat
@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer implements ExtCoreMinecraftServer {
	
	@Unique
	protected ImplMPCR root;
	
	@Inject(method = "saveAllWorlds", at = @At("HEAD"))
	private void on_saveAllWorlds_head(CallbackInfo ci) throws IOException {
		this.root.saveDat();
	}
	
	@Inject(method = "initialWorldChunkLoad", at = @At("HEAD"))
	private void on_initialWorldChunkLoad_head(CallbackInfo ci) throws IOException {
		this.root.loadDat();
	}
	
	@Override
	public ImplMPCR wolftail_getRootManager() {
		return this.root;
	}
}