package net.wolftail.internal.core.mixin;

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

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.wolftail.internal.core.ExtCoreMinecraftServer;
import net.wolftail.internal.core.ImplMPCR;
import net.wolftail.internal.core.ImplPCS;

//server side steve logout
@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer {
	
	@Final
	@Shadow
	public MinecraftServer serverController;
	
	@Shadow
	public EntityPlayerMP player;
	
	@Unique
	private static final Logger logger = LogManager.getLogger("Wolftail/User");
	
	@Inject(method = "onDisconnect", at = @At(value = "INVOKE", target = "net.minecraft.server.MinecraftServer.isSinglePlayer()Z"))
	private void on_onDisconnect_invoke_isSinglePlayer(CallbackInfo ci) {
		ImplMPCR root = ((ExtCoreMinecraftServer) this.serverController).wolftail_getRootManager();
		ImplPCS pc = root.contextFor(this.player.getUniqueID());
		
		if (pc != null) // disconnect during FML handshake
			root.logout(pc);
	}
	
	@Redirect(method = "onDisconnect", at = @At(value = "FIELD", target = "net.minecraft.network.NetHandlerPlayServer.LOGGER:Lorg/apache/logging/log4j/Logger;", opcode = Opcodes.GETSTATIC, ordinal = 0))
	private Logger proxy_onDisconnect_getStatic_LOGGER_0() {
		return logger;
	}
}
