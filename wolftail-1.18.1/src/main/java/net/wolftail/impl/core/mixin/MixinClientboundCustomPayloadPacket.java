package net.wolftail.impl.core.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;

//MC BUG FIX: reduce copy
@Mixin(ClientboundCustomPayloadPacket.class)
public abstract class MixinClientboundCustomPayloadPacket {
	
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
	
	@Overwrite
	public FriendlyByteBuf getData() {
		return this.data;
	}
}
