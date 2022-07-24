package net.wolftail.impl.core.mixin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.wolftail.impl.core.ExtCoreMinecraftServer;
import net.wolftail.impl.core.ImplMPCR;

//server side steve logout
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class MixinServerGamePacketListenerImpl {
	
	@Final
	@Shadow
	public MinecraftServer server;
	
	@Shadow
	public ServerPlayer player;
	
	@Unique
	private static final Logger logger = LogManager.getLogger("Wolftail/User");
	
	@Inject(method = "onDisconnect", at = @At(value = "INVOKE", target = "isSingleplayerOwner()Z"))
	private void on_onDisconnect_invoke_isSingleplayerOwner(CallbackInfo ci) {
		ImplMPCR root = ((ExtCoreMinecraftServer) this.server).wolftail_getRootManager();
		
		root.logout(root.contextFor(this.player.getGameProfile().getId()));
	}
	
	@Redirect(method = "onDisconnect", at = @At(value = "FIELD", target = "LOGGER", opcode = Opcodes.GETSTATIC, ordinal = 0))
	private Logger proxy_onDisconnect_getStatic_LOGGER_0() {
		return logger;
	}
}
