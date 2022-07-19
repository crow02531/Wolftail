package net.wolftail.impl.core.mixin;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.wolftail.impl.core.ExtCoreMinecraftServer;
import net.wolftail.impl.core.ImplMPCR;

//rootManager::onServerStopping; add root manager to server
@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer implements ExtCoreMinecraftServer {
	
	@Unique
	protected ImplMPCR root;
	
	@Inject(method = "stopServer", at = @At(value = "FIELD", target = "playerList:Lnet/minecraft/server/management/PlayerList;", opcode = Opcodes.GETFIELD, ordinal = 0))
	private void onStopServer(CallbackInfo info) throws FileNotFoundException, IOException {
		this.root.onServerStopping();
	}
	
	@Override
	public ImplMPCR wolftail_getRootManager() {
		return this.root;
	}
}
