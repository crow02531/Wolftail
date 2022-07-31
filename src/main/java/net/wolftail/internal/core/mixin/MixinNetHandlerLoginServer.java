package net.wolftail.internal.core.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import io.netty.buffer.Unpooled;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.NetHandlerLoginServer;
import net.wolftail.internal.core.ExtCoreMinecraftServer;
import net.wolftail.internal.core.ImplPCS;
import net.wolftail.internal.core.ImplUPT;
import net.wolftail.internal.core.network.NptServerPacketListener;

//accept uniplayer in server side
@Mixin(NetHandlerLoginServer.class)
public abstract class MixinNetHandlerLoginServer {
	
	@Final
	@Shadow
	public MinecraftServer server;
	
	@Final
	@Shadow
	public NetworkManager networkManager;
	
	@Shadow
	public GameProfile loginGameProfile;
	
	@Inject(method = "tryAcceptPlayer", at = @At(value = "INVOKE", target = "net.minecraft.network.NetworkManager.sendPacket(Lnet/minecraft/network/Packet;)V", shift = Shift.AFTER), cancellable = true)
	private void on_tryAcceptPlayer_invokeAfter_sendPacket_1(CallbackInfo ci) {
		GameProfile profile = this.loginGameProfile;
		NetworkManager connect = this.networkManager;
		
		// now it was LOGIN state and vanilla connection has just set up
		// we should in LOGIC_SERVER thread
		
		ImplPCS context = ((ExtCoreMinecraftServer) this.server).wolftail_getRootManager().login(connect,
				profile.getId(), profile.getName());
		ImplUPT type = context.playType();
		
		// the connection state setting action will be executed in netty's event loop
		// thread, it will be set to PLAY
		connect.sendPacket(newTypeNotifyPacket(type));
		
		if (!type.isPlayerType()) {
			ci.cancel();
			
			connect.setNetHandler(new NptServerPacketListener(context));
			type.callServerEnter(context);
		}
	}
	
	@Unique
	private static SPacketCustomPayload newTypeNotifyPacket(ImplUPT type) {
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
		buf.writeResourceLocation(type.registeringId());
		
		return new SPacketCustomPayload("WT|TN", buf);
	}
}
