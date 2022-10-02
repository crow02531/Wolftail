package com.example.examplemod;

import java.lang.reflect.Method;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.wolftail.api.IClientHandler;
import net.wolftail.api.INetworkHandler;
import net.wolftail.api.PlayContext;

public final class PigClientHandler implements IClientHandler, INetworkHandler {

	static final PigClientHandler INSTANCE = new PigClientHandler();

	private static int frameCount;

	private PlayContext playContext;
	private WorldClient world;

	private PigClientHandler() {
	}

	@Override
	public void handleEnter(PlayContext context) {
		this.playContext = context;
		context.setHandler(this);

		Minecraft mc = Minecraft.getMinecraft();

		this.world = mc.world = new WorldClient(null, new WorldSettings(0, null, false, false, WorldType.FLAT), 0,
				null, mc.mcProfiler);
		mc.renderGlobal.setWorldAndLoadRenderers(this.world);
		mc.effectRenderer.clearEffects(this.world);
		TileEntityRendererDispatcher.instance.setWorld(this.world);
		MinecraftForgeClient.clearRenderCache();

		mc.player = new EntityPlayerSP(mc, mc.world, new NetHandlerPlayClient(mc, null, null, mc.getSession().getProfile()), null, null);
		mc.player.width = 0;
		mc.player.height = 0;
		mc.player.eyeHeight = 0;
		mc.playerController = new PlayerControllerMP(mc, mc.player.connection);
		mc.setRenderViewEntity(mc.player);

		for(int x = 0; x < 4; ++x) {
			for(int z = 0; z < 4; ++z) {
				world.doPreChunk(x - 2, z - 2, true);
			}
		}

		for (BlockPos p : BlockPos.getAllInBoxMutable(-8, 0, -8, 8, 1, 8))
			world.setBlockState(p, Blocks.BEDROCK.getDefaultState());
		
		for (BlockPos p : BlockPos.getAllInBoxMutable(-4, 2, -4, 4, 2, 4))
			world.setBlockState(p, Blocks.GLASS.getDefaultState());
	}

	@Override
	public void handleFrame() {
		Minecraft mc = Minecraft.getMinecraft();

		int j = Math.min(Minecraft.getDebugFPS(), mc.gameSettings.limitFramerate);
		j = Math.max(j, 60);
		long l = Math.max((long) (1000000000 / j / 4), 0L);

		GlStateManager.clearColor(0, 0, 0, 0);
		GlStateManager.clearDepth(1);
		GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		renderWorld(mc, mc.renderGlobal, mc.getRenderPartialTicks(), System.nanoTime() + l);
	}

	private static void renderWorld(Minecraft mc, RenderGlobal rg, float partialTicks, long finishTimeNano) {
		GlStateManager.enableDepth();
        GlStateManager.enableBlend();
		
		double x = -14;
		double y = 14;
		double z = 0;
		double yaw = -80;
		double pitch = 45;
		double roll = 45;

		// setup opnegl transform
		{
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			Project.gluPerspective(90, (float) mc.displayWidth / (float) mc.displayHeight, 0.05F,
					(float) (mc.gameSettings.renderDistanceChunks * 16) * MathHelper.SQRT_2);

			GlStateManager.matrixMode(5888);
			GlStateManager.loadIdentity();
			GlStateManager.rotate((float) roll, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate((float) pitch, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate((float) yaw + 180, 0.0F, 1.0F, 0.0F);
		}

		mc.player.setLocationAndAngles(x, y, z, (float) yaw, (float) pitch);
		ClippingHelperImpl.getInstance();
		ActiveRenderInfo.updateRenderInfo(mc.player, mc.gameSettings.thirdPersonView == 2);

		// draw sky
		{
			Vec3d skyColor = mc.world.getSkyColor(mc.getRenderViewEntity(), partialTicks);
			GlStateManager.clearColor((float) skyColor.x, (float) skyColor.y, (float) skyColor.z, 0);
			GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);
			
			GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            Project.gluPerspective(90, (float) mc.displayWidth / (float) mc.displayHeight, 0.05F, (float) (mc.gameSettings.renderDistanceChunks * 16) * 2.0F);
            GlStateManager.matrixMode(5888);
			
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            rg.renderSky(partialTicks, 2);
			GlStateManager.disableAlpha();
			GlStateManager.disableFog();
			
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            Project.gluPerspective(90, (float) mc.displayWidth / (float) mc.displayHeight, 0.05F,
					(float) (mc.gameSettings.renderDistanceChunks * 16) * MathHelper.SQRT_2);
            GlStateManager.matrixMode(5888);
		}

		// update light map
		mc.entityRenderer.updateRenderer();
		try {
			Method m = EntityRenderer.class.getDeclaredMethod("updateLightmap", float.class);
			m.setAccessible(true);

			m.invoke(mc.entityRenderer, partialTicks);
		} catch(Throwable e) {
			e.printStackTrace();
		}

		// build terrain
		ICamera icamera = new Frustum();
		icamera.setPosition(x, y, z);
		rg.setupTerrain(mc.player, partialTicks, icamera, frameCount++, false);
		rg.updateChunks(finishTimeNano);

		// draw terrain
		mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		GlStateManager.disableBlend();
		GlStateManager.enableCull();
		rg.renderBlockLayer(BlockRenderLayer.SOLID, partialTicks, 2, mc.player);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, mc.gameSettings.mipmapLevels > 0);
        rg.renderBlockLayer(BlockRenderLayer.CUTOUT_MIPPED, partialTicks, 2, mc.player);
        mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        rg.renderBlockLayer(BlockRenderLayer.CUTOUT, partialTicks, 2, mc.player);
        mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
		rg.renderBlockLayer(BlockRenderLayer.TRANSLUCENT, partialTicks, 2, mc.player);
	}

	@Override
	public void handleChat(ChatType type, ITextComponent text) {
	}

	@Override
	public void handleLeave() {
		this.playContext = null;

		MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(this.world));
		this.world = Minecraft.getMinecraft().world = null;
	}

	@Override
	public void handle(ByteBuf buf) {

	}

	@Override
	public void tick() {
		this.world.setWorldTime(System.currentTimeMillis() % 24000);
	}
}
