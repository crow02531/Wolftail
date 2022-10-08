package net.wolftail.util.client.renderer;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.wolftail.api.IClientHandler;
import net.wolftail.api.PlayContext;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;

@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_CLIENT)
public abstract class VanillaClientHandler implements IClientHandler {

    private VanillaUpdater updater;

    @Override
    public final void handleEnter(@Nonnull PlayContext context) {
        Minecraft mc = Minecraft.getMinecraft();

        WorldClient w = mc.world = new WorldClient(null, new WorldSettings(0, null, false, false, WorldType.FLAT), 0,
                null, mc.mcProfiler);

        EntityPlayerSP p = mc.player = new EntityPlayerSP(mc, w,
                new NetHandlerPlayClient(mc, null, null, mc.getSession().getProfile()), null, null);
        p.width = 0;
        p.height = 0;
        p.eyeHeight = 0;
        p.setLocationAndAngles(0, 0, 0, 0, 0);

        mc.playerController = new PlayerControllerMP(mc, p.connection);

        mc.setRenderViewEntity(p);
        mc.renderGlobal.setWorldAndLoadRenderers(w);
        mc.effectRenderer.clearEffects(w);

        this.updater = new VanillaUpdater(w, p);

        this.handleEnter0(context);
    }

    @Override
    public final void handleFrame() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityRenderer er = mc.entityRenderer;
        RenderGlobal rg = mc.renderGlobal;
        ParticleManager re = mc.effectRenderer;
        EntityPlayerSP p = mc.player;
        float pt = mc.getRenderPartialTicks();

        float aspect = (float) mc.displayWidth / (float) mc.displayHeight;
        float fovy = mix(p.prevCameraPitch, p.cameraPitch, pt);
        double x = mix(p.prevPosX, p.posX, pt);
        double y = mix(p.prevPosY, p.posY, pt);
        double z = mix(p.prevPosZ, p.posZ, pt);

        er.farPlaneDistance = mc.gameSettings.renderDistanceChunks * 16;
        mc.setRenderViewEntity(mc.player);

        // setup root transform
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        Project.gluPerspective(fovy, aspect, 0.05F, er.farPlaneDistance * MathHelper.SQRT_2);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glRotatef(mix(p.prevCameraYaw, p.cameraYaw, pt), 0, 0, 1);
        GL11.glRotatef(mix(p.prevRotationPitch, p.rotationPitch, pt), 1, 0, 0);
        GL11.glRotatef(mix(p.prevRotationYaw, p.rotationYaw, pt) + 180, 0, 1, 0);

        // setup transform cache
        ClippingHelperImpl.getInstance();
        ActiveRenderInfo.updateRenderInfo(p, false);
        Frustum icamera = new Frustum();
        icamera.setPosition(x, y, z);

        // update light map etc.
        er.updateRenderer();
        er.updateFogColor(pt);
        er.updateLightmap(pt);

        // draw sky, clouds & clear depth buffer to 1
        {
            // updateFogColor has set gl's clear color
            GlStateManager.clearDepth(1);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            Project.gluPerspective(fovy, aspect, 0.05F, er.farPlaneDistance * 2);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPushMatrix();

            // render sky
            er.setupFog(-1, pt); // setup fog for rendering sky
            GlStateManager.disableCull();
            rg.renderSky(pt, 2);
            GlStateManager.disableAlpha();

            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            Project.gluPerspective(fovy, aspect, 0.05F, er.farPlaneDistance * 4);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);

            // render clouds
            er.setupFog(0, pt); // setup fog for normal rendering
            rg.renderClouds(pt, 2, x, y, z);

            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();
        }

        // build terrain
        rg.setupTerrain(p, pt, icamera, er.frameCount++, false);
        rg.updateChunks(Long.MAX_VALUE);

        // draw terrain
        {
            ITextureObject tex_blocks = bindAndGetTexture(mc.renderEngine, TextureMap.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.enableCull();
            GlStateManager.enableDepth();

            GlStateManager.disableBlend();
            rg.renderBlockLayer(BlockRenderLayer.SOLID, pt, 2, p);

            GlStateManager.enableBlend();
            GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

            GlStateManager.enableAlpha();
            tex_blocks.setBlurMipmap(false, mc.gameSettings.mipmapLevels > 0);
            rg.renderBlockLayer(BlockRenderLayer.CUTOUT_MIPPED, pt, 2, p);

            tex_blocks.restoreLastBlurMipmap();
            tex_blocks.setBlurMipmap(false, false);
            rg.renderBlockLayer(BlockRenderLayer.CUTOUT, pt, 2, p);

            tex_blocks.restoreLastBlurMipmap();
            rg.renderBlockLayer(BlockRenderLayer.TRANSLUCENT, pt, 2, p);
        }

        // draw entities
        GlStateManager.disableCull();
        RenderHelper.enableStandardItemLighting();
        ForgeHooksClient.setRenderPass(0);
        rg.renderEntities(p, icamera, pt);
        ForgeHooksClient.setRenderPass(1);
        rg.renderEntities(p, icamera, pt);
        ForgeHooksClient.setRenderPass(-1);

        // draw particles & rain snow
        RenderHelper.disableStandardItemLighting();
        er.enableLightmap();
        re.renderParticles(p, pt);
        re.renderLitParticles(p, pt);
        er.disableLightmap();
        er.renderRainSnow(pt);

        // call custom frame
        this.handleFrame0();
    }

    @Override
    public final void handleTick() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP p = mc.player;
        WorldClient w = mc.world;

        mc.renderEngine.tick();
        mc.effectRenderer.updateEffects();
        mc.getMusicTicker().update();

        p.prevPosX = p.lastTickPosX = p.posX;
        p.prevPosY = p.lastTickPosY = p.posY;
        p.prevPosZ = p.lastTickPosZ = p.posZ;
        p.prevRotationYaw = p.rotationYaw;
        p.prevRotationPitch = p.rotationPitch;
        p.prevCameraYaw = p.cameraYaw;
        p.prevCameraPitch = p.cameraPitch;

        w.tick();
        w.updateEntities();
        w.doVoidFogParticles(MathHelper.floor(p.posX), MathHelper.floor(p.posY), MathHelper.floor(p.posZ));
        w.prevRainingStrength = w.rainingStrength;
        w.prevThunderingStrength = w.thunderingStrength;

        mc.renderGlobal.updateClouds();

        this.handleTick0();
    }

    @Override
    public final void handleLeave() {
        Minecraft mc = Minecraft.getMinecraft();

        MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(mc.world));
        this.handleLeave0();

        mc.player = null;
        mc.world = null;
        mc.playerController = null;
        mc.setRenderViewEntity(null);
        mc.renderGlobal.setWorldAndLoadRenderers(null);
        mc.effectRenderer.clearEffects(null);
        TileEntityRendererDispatcher.instance.setWorld(null);
        this.updater = null;

        System.gc();
    }

    @Nonnull
    protected final VanillaUpdater getUpdater() {
        return this.updater;
    }

    protected abstract void handleEnter0(@Nonnull PlayContext context);

    protected abstract void handleFrame0();

    protected abstract void handleTick0();

    protected abstract void handleLeave0();

    private static float mix(float a, float b, float i) {
        return a + (b - a) * i;
    }

    private static double mix(double a, double b, double i) {
        return a + (b - a) * i;
    }

    private static ITextureObject bindAndGetTexture(TextureManager tm, ResourceLocation l) {
        tm.bindTexture(l);

        return tm.getTexture(l);
    }
}