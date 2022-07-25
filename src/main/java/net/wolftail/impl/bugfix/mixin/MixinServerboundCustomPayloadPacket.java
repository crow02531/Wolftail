package net.wolftail.impl.bugfix.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.server.RunningOnDifferentThreadException;

//MC BUG FIX: reduce copy, prevent memory leak when handleCustomPayload catches exception
@Mixin(ServerboundCustomPayloadPacket.class)
public abstract class MixinServerboundCustomPayloadPacket {
	
	@Final
	@Shadow
	public FriendlyByteBuf data;
	
	@Redirect(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At(value = "INVOKE", target = "readBytes(I)Lio/netty/buffer/ByteBuf;"))
	private ByteBuf proxy_constructor_readBytes(FriendlyByteBuf buf, int i) {
		return buf.readRetainedSlice(i);
	}
	
	@Overwrite
	public void handle(ServerGamePacketListener serverGamePacketListener) {
		try {
			serverGamePacketListener.handleCustomPayload((ServerboundCustomPayloadPacket) (Object) this);
		} catch(RunningOnDifferentThreadException e) {
			throw e;
		} catch(Throwable e) {
			this.data.release();
			
			throw e;
		}
	}
}
