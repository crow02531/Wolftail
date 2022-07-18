package net.wolftail.impl.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.server.MinecraftServer;
import net.wolftail.impl.core.ExtCoreMinecraftServer;
import net.wolftail.impl.core.ImplMPCR;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer implements ExtCoreMinecraftServer {
	
	@Unique
	protected ImplMPCR root;
	
	@Override
	public ImplMPCR wolftail_getRootManager() {
		return this.root;
	}
}
