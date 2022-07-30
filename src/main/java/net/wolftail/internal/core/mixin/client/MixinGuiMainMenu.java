package net.wolftail.internal.core.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;

//draw 'Wolftail Installed' in main menu
@Mixin(GuiMainMenu.class)
public abstract class MixinGuiMainMenu extends GuiScreen {
	
	@Inject(method = "drawScreen", at = @At("RETURN"))
	private void on_drawScreen_return(CallbackInfo info) {
		this.drawString(this.fontRenderer, "Wolftail Installed", 2, 2, -1);
	}
}
