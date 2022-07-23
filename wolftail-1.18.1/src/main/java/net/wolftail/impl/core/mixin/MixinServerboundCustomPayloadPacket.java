package net.wolftail.impl.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;

//MC BUG FIX: reduce copy
@Mixin(ServerboundCustomPayloadPacket.class)
public abstract class MixinServerboundCustomPayloadPacket {
	
	@Redirect(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At(value = "INVOKE", target = "readBytes(I)Lio/netty/buffer/ByteBuf;"))
	private ByteBuf proxy_constructor_readBytes(FriendlyByteBuf buf, int i) {
		return buf.readRetainedSlice(i);
	}
}
