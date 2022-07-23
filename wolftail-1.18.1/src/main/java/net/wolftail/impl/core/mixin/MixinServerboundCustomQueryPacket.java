package net.wolftail.impl.core.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;

//MC BUG FIX: reduce copy; prevent memory leak
@Mixin(ServerboundCustomQueryPacket.class)
public abstract class MixinServerboundCustomQueryPacket {
	
	@Final
	@Shadow
	public FriendlyByteBuf data;
	
	@Redirect(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At(value = "INVOKE", target = "readBytes(I)Lio/netty/buffer/ByteBuf;"))
	private ByteBuf proxy_constructor_readBytes(FriendlyByteBuf buf, int i) {
		return buf.readRetainedSlice(i);
	}
	
	@Redirect(method = "write", at = @At(value = "INVOKE", target = "copy()Lio/netty/buffer/ByteBuf;"))
	private ByteBuf proxy_write_copy(FriendlyByteBuf buf) {
		return buf;
	}
	
	@Inject(method = "handle", at = @At("RETURN"))
	private void on_handle_return(CallbackInfo ci) {
		if(this.data != null)
			this.data.release();
	}
}
