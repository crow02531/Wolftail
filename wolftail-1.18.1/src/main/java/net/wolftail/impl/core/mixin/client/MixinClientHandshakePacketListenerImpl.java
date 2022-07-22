package net.wolftail.impl.core.mixin.client;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.resources.ResourceLocation;
import net.wolftail.api.UniversalPlayerTypeRegistry;
import net.wolftail.impl.core.ExtCoreMinecraft;
import net.wolftail.impl.core.ImplUPT;
import net.wolftail.impl.core.network.Constants;

//accept type notify packet; intercept handling game profile
@Mixin(ClientHandshakePacketListenerImpl.class)
public abstract class MixinClientHandshakePacketListenerImpl {
	
	@Final
	@Shadow
	public Connection connection;
	
	@Shadow
	public GameProfile localGameProfile;
	
	@Unique
	private static final Logger logger = LogManager.getLogger("Wolftail/Network");
	
	@Unique
	private ImplUPT type;
	
	@Inject(method = "handleCustomQuery", at = @At(value = "INVOKE", target = "accept(Lnet/minecraft/network/chat/Component;)V", shift = Shift.AFTER), cancellable = true)
	private void on_handleCustomQuery_invokeAfter_accept(ClientboundCustomQueryPacket packet, CallbackInfo ci) throws IOException {
		if(packet.getIdentifier().equals(Constants.CHANNEL_TYPE_NOTIFY)) {
			ci.cancel();
			
			ResourceLocation typeId = packet.getData().readResourceLocation();
			
			if(packet.getData().isReadable())
				throw new IOException();
			
			if((this.type = (ImplUPT) UniversalPlayerTypeRegistry.INSTANCE.registeredAt(typeId)) == null)
				throw new IllegalStateException("Unknow universal player type " + typeId);
		}
	}
	
	@Inject(method = "handleGameProfile", at = @At(value = "INVOKE", target = "setListener(Lnet/minecraft/network/PacketListener;)V"), cancellable = true)
	private void on_handleGameProfile_invoke_setListener(CallbackInfo ci) throws InterruptedException, ExecutionException {
		if(this.type == null) {
			logger.warn("Expecting a type notify packet before receiving game profile packet, maybe the server dosen't install wolftail.");
			
			return;
		}
		
		//we now should in netty thread
		((ExtCoreMinecraft) Minecraft.getInstance()).wolftail_loadContext(this.type,
				this.localGameProfile.getId(), this.localGameProfile.getName(),
				this.connection);
		
		if(!this.type.isPlayerType())
			ci.cancel();
	}
}
