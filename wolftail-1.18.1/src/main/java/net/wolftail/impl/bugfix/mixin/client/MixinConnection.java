package net.wolftail.impl.bugfix.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;

//MC BUG FIX: client sends clientbound disconnect packet to server when catching exception
@Mixin(Connection.class)
public abstract class MixinConnection {
	
	@Final
	@Shadow
	public PacketFlow receiving;
	
	@Shadow
	public abstract void setReadOnly();
	
	@Shadow
	public abstract void disconnect(Component component);
	
	@Inject(method = "exceptionCaught", at = @At(value = "INVOKE", target = "getCurrentProtocol()Lnet/minecraft/network/ConnectionProtocol;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void on_exceptionCaught_invoke_getCurrentProtocol(ChannelHandlerContext arg0, Throwable throwable, CallbackInfo ci, boolean bl, Component component) {
		if(this.receiving == PacketFlow.CLIENTBOUND) {
			ci.cancel();
			
			this.disconnect(component);
			this.setReadOnly();
		}
	}
}
