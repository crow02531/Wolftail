package net.wolftail.impl.core.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;

//draw 'Wolftail Installed' in main menu
@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen {
	
	protected MixinTitleScreen(Component component) {
		super(component);
	}
	
	@Inject(method = "render", at = @At(value = "INVOKE", target = "drawString(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V", ordinal = 0))
	private void on_render_invoke_drawString_0(PoseStack poseStack, int i, int j, float f, CallbackInfo ci) {
		GuiComponent.drawString(poseStack, this.font, "Wolftail Installed", 2, 2, -1);
	}
}
