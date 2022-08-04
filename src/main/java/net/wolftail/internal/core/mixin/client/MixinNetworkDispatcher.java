package net.wolftail.internal.core.mixin.client;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher.ConnectionType;
import net.wolftail.internal.core.network.TransientPacketListener;

//client side intercepts completion
@Mixin(NetworkDispatcher.class)
public abstract class MixinNetworkDispatcher {
	
	@Final
	@Shadow(remap = false)
	public NetworkManager manager;
	
	@Inject(method = "completeClientSideConnection", at = @At(value = "FIELD", target = "net.minecraftforge.fml.common.network.handshake.NetworkDispatcher.manager:Lnet/minecraft/network/NetworkManager;", opcode = Opcodes.GETFIELD, remap = false), cancellable = true, remap = false)
	private void on_completeClientSideConnection_getField_manager(ConnectionType type, CallbackInfo ci) {
		ci.cancel();
		
		NetworkManager c = this.manager;
		
		c.setNetHandler(new TransientPacketListener((NetHandlerPlayClient) c.getNetHandler(), type));
	}
}
