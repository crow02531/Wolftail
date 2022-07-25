package net.wolftail.impl.bugfix.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ServerLoginPacketListener;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.server.RunningOnDifferentThreadException;

//MC BUG FIX: reduce copy, prevent memory leak
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
	
	@Overwrite
	public void handle(ServerLoginPacketListener serverLoginPacketListener) {
		try {
			serverLoginPacketListener.handleCustomQueryPacket((ServerboundCustomQueryPacket) (Object) this);
		} catch(RunningOnDifferentThreadException e) {
			throw e;
		} catch(Throwable e) {
			if(this.data != null)
				this.data.release();
			
			throw e;
		}
	}
}