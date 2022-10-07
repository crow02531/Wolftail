package net.wolftail.util.client.renderer;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.glu.Project;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;

@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_CLIENT)
public final class VanillaUnit extends UIUnit {

    private WorldClient world;
    private EntityPlayerSP player;
    private float player_roll;
    private float player_fovy;

    private RenderGlobal render_global;
    private int frame_count;

    private int object_rb;

    private void create_rb() {
        this.object_rb = GL30.glGenRenderbuffers();

        int old_binding = GL11.glGetInteger(GL30.GL_RENDERBUFFER_BINDING);
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, this.object_rb);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH_COMPONENT32F, this.param_width,
                this.param_height);
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, old_binding);
    }

    private void delete_rb() {
        GL30.glDeleteRenderbuffers(this.object_rb);

        this.object_rb = 0;
    }

    private void bind_rb2fb() {
        this.bindAndExecute(() -> {
            GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER,
                    object_rb);
        });
    }

    public VanillaUnit(int pw, int ph) {
        super(pw, ph);

        this.create_rb();
        this.bind_rb2fb();

        Minecraft mc = Minecraft.getMinecraft();

        // create world & player
        this.world = new WorldClient(null, new WorldSettings(0, null, false, false, WorldType.FLAT), 0, null, mc.mcProfiler);
        this.player = new EntityPlayerSP(mc, this.world,
                new NetHandlerPlayClient(mc, null, null, mc.getSession().getProfile()), null, null);
        this.player.width = 0;
        this.player.height = 0;
        this.player.eyeHeight = 0;

        // create render global
        this.render_global = new RenderGlobal(mc);
        World o_w = mc.getRenderManager().world;
        this.render_global.setWorldAndLoadRenderers(this.world);
        mc.getRenderManager().world = o_w;
    }

    public void pCamera(double x, double y, double z, float yaw, float pitch, float roll, float fovy) {
        this.player.setLocationAndAngles(x, y, z, yaw, pitch);
        this.player_roll = roll;
        this.player_fovy = fovy;
    }

    public void pTime(int time) {
        this.world.setWorldTime(time);
    }

    public void pWeather(float rainStr, float thunderStr) {
        WorldClient w = this.world;

        w.prevRainingStrength = w.rainingStrength = rainStr;
        w.prevThunderingStrength = w.thunderingStrength = thunderStr;
    }

    public void pSetState(@Nonnull BlockPos pos, IBlockState state) {
        int cx = pos.getX() >> 4;
        int cz = pos.getZ() >> 4;
        state = state == null ? Blocks.AIR.getDefaultState() : state;

        ChunkProviderClient cp = this.world.getChunkProvider();

        for(int x = -1; x <= 1; ++x) {
            for(int z = -1; z <= 1; ++z) {
                if(!cp.isChunkGeneratedAt(cx + x, cz + z))
                    cp.loadChunk(cx + x, cz + z);
            }
        }

        this.world.setBlockState(pos, state);
    }

    public void pSetSection(int chunkX, int chunkZ, int index, ByteBuf buf) {

    }

    public void pSetSection(int chunkX, int chunkZ, int index, ExtendedBlockStorage src) {

    }

    public void pSetChunk(int chunkX, int chunkZ, Chunk src) {

    }

    public void pClear() {
        
    }

    @Override
    void release0() {
        this.delete_rb();

        this.world = null;
        this.player = null;
        this.render_global = null;
        
        System.gc();
    }

    @Override
    void resize0(int oldWidth, int oldHeight) {
        this.delete_rb();
        this.create_rb();
        this.bind_rb2fb();
    }

    @Override
    void flush0() {
        Minecraft mc = Minecraft.getMinecraft();

        // cache states
        boolean o_f = mc.gameSettings.fboEnable;
        WorldClient o_w = mc.world;
        EntityPlayerSP o_p = mc.player;
        Entity o_rv = mc.getRenderViewEntity();
        RenderGlobal o_rg = mc.renderGlobal;
        GL11.glPushAttrib(GL11.GL_VIEWPORT_BIT | GL11.GL_TRANSFORM_BIT);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();

        // setup basic states
        mc.gameSettings.fboEnable = false;
        mc.world = this.world;
        mc.player = this.player;
        mc.setRenderViewEntity(this.player);
        mc.renderGlobal = this.render_global;

        // flush
        this.bindAndExecute(this::flush0_ext);

        // restore states
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
        mc.gameSettings.fboEnable = o_f;
        mc.world = o_w;
        mc.player = o_p;
        mc.setRenderViewEntity(o_rv);
        mc.renderGlobal = o_rg;
        TileEntityRendererDispatcher.instance.setWorld(null);
    }

    private void flush0_ext() {
        Minecraft mc = Minecraft.getMinecraft();
        WorldClient world = this.world;
        EntityPlayerSP player = this.player;
        RenderGlobal rg = this.render_global;
        float aspect = (float) this.param_width / (float) this.param_height;
        float zFar_base = mc.gameSettings.renderDistanceChunks * 16;

        // set viewport
        GL11.glViewport(0, 0, this.param_width, this.param_height);

        // setup root transform
        {
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            Project.gluPerspective(this.player_fovy, aspect, 0.05F, zFar_base * MathHelper.SQRT_2);

            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            GL11.glRotatef(this.player_roll, 0, 0, 1);
            GL11.glRotatef(player.rotationPitch, 1, 0, 0);
            GL11.glRotatef(player.rotationYaw + 180, 0, 1, 0);
        }

        // setup cache
        ClippingHelperImpl.getInstance();
        ActiveRenderInfo.updateRenderInfo(player, false);

        // update light map
        mc.entityRenderer.lightmapUpdateNeeded = true;
        mc.entityRenderer.updateLightmap(0);

        // draw sky & clear depth buffer to 1
        {
            Vec3d skyColor = world.getSkyColor(player, 0);
            GlStateManager.clearColor((float) skyColor.x, (float) skyColor.y, (float) skyColor.z, 0);
            GlStateManager.clearDepth(1);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            Project.gluPerspective(this.player_fovy, aspect, 0.05F, zFar_base * 2);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);

            rg.renderSky(0, 2);
            GlStateManager.disableAlpha();
            GlStateManager.disableFog();

            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
        }

        // build terrain
        {
            ICamera icamera = new Frustum();
            icamera.setPosition(player.posX, player.posY, player.posZ);

            rg.setupTerrain(player, 0, icamera, this.frame_count++, false);
            rg.updateChunks(Long.MAX_VALUE);
        }

        // draw terrain
        {
            ITextureObject tex_blocks = bindAndGetTexture(mc.renderEngine, TextureMap.LOCATION_BLOCKS_TEXTURE);
			GlStateManager.enableCull();
            GlStateManager.enableDepth();

			GlStateManager.disableBlend();
			rg.renderBlockLayer(BlockRenderLayer.SOLID, 0, 2, player);

			GlStateManager.enableBlend();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

			tex_blocks.setBlurMipmap(false, mc.gameSettings.mipmapLevels > 0);
			rg.renderBlockLayer(BlockRenderLayer.CUTOUT_MIPPED, 0, 2, player);

			tex_blocks.restoreLastBlurMipmap();
			tex_blocks.setBlurMipmap(false, false);
			rg.renderBlockLayer(BlockRenderLayer.CUTOUT, 0, 2, player);

			tex_blocks.restoreLastBlurMipmap();
			rg.renderBlockLayer(BlockRenderLayer.TRANSLUCENT, 0, 2, player);
        }
    }

    private static ITextureObject bindAndGetTexture(TextureManager tm, ResourceLocation l) {
		tm.bindTexture(l);

		return tm.getTexture(l);
	}
}
