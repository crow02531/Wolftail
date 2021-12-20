package net.wolftail.impl.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;

@Mixin(GuiMainMenu.class)
public abstract class MixinGuiMainMenu extends GuiScreen {
	
	@Inject(method = "drawScreen", at = @At("RETURN"))
	private void onDrawScreen(CallbackInfo info) {
		this.drawString(this.fontRenderer, "Wolftail Installed", 2, 2, -1);
	}
}
