package net.wolftail.internal.renderer.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.renderer.EntityRenderer;
import net.wolftail.internal.renderer.ExtEntityRenderer;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer implements ExtEntityRenderer {

    @Shadow
    public boolean lightmapUpdateNeeded;

    @Shadow
    public int rendererUpdateCount;

    @Shadow
    public abstract void updateLightmap(float partialTicks);

    @Shadow
    public abstract void renderRainSnow(float partialTicks);

    @Override
    public void wolftail_forceUpdateLightmap(float partialTicks) {
        this.lightmapUpdateNeeded = true;
        this.updateLightmap(partialTicks);
    }

    @Override
    public void wolftail_renderRainSnow(float partialTicks) {
        this.renderRainSnow(partialTicks);
    }

    @Override
    public void wolftail_rendererUpdateCount_set(int v) {
        this.rendererUpdateCount = v;
    }
}
