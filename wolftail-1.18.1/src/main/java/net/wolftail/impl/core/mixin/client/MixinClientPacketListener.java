package net.wolftail.impl.core.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;

//MC BUG FIX: singleplayer network crash makes you back to JoinMultiplayerScreen
@Mixin(ClientPacketListener.class)
public abstract class MixinClientPacketListener {
	
	@Final
	@Shadow
	public Connection connection;
	
	@Redirect(method = "onDisconnect", at = @At(value = "NEW", target = "Lnet/minecraft/client/gui/screens/DisconnectedScreen;", ordinal = 1))
	private DisconnectedScreen proxy_onDisconnect_newDisconnectedScreen_1(Screen s, Component c0, Component c1) {
		return new DisconnectedScreen(this.connection.isMemoryConnection() ? new TitleScreen() : s, c0, c1);
	}
}
