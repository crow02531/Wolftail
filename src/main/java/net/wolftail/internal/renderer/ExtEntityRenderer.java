package net.wolftail.internal.renderer;

public interface ExtEntityRenderer {
    
    void wolftail_forceUpdateLightmap(float partialTicks);
    void wolftail_renderRainSnow(float partialTicks);
    void wolftail_rendererUpdateCount_set(int v);
}
