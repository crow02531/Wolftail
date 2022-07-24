package net.wolftail.impl.bugfix.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;

//MC BUG FIX: singleplayer network crash makes you back to JoinMultiplayerScreen,
//move byteBuf.release() to ClientboundCustomPayloadPacket
@Mixin(ClientPacketListener.class)
public abstract class MixinClientPacketListener {
	
	@Final
	@Shadow
	public Connection connection;
	
	@Redirect(method = "onDisconnect", at = @At(value = "NEW", target = "Lnet/minecraft/client/gui/screens/DisconnectedScreen;", ordinal = 1))
	private DisconnectedScreen proxy_onDisconnect_newDisconnectedScreen_1(Screen s, Component c0, Component c1) {
		return new DisconnectedScreen(this.connection.isMemoryConnection() ? new TitleScreen() : s, c0, c1);
	}
	
	@Inject(method = "handleCustomPayload", at = @At(value = "INVOKE", target = "release()Z"), cancellable = true)
	private void on_handleCustomPayload_invoke_release(CallbackInfo ci) {
		ci.cancel();
	}
}
