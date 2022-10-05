package com.example.examplemod;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.wolftail.api.IClientHandler;
import net.wolftail.api.INetworkHandler;
import net.wolftail.api.PlayContext;
import net.wolftail.internal.renderer.ExtEntityRenderer;

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

		mc.player = new EntityPlayerSP(mc, mc.world,
				new NetHandlerPlayClient(mc, null, null, mc.getSession().getProfile()), null, null);
		mc.player.width = 0;
		mc.player.height = 0;
		mc.player.eyeHeight = 0;
		mc.playerController = new PlayerControllerMP(mc, mc.player.connection);
		mc.setRenderViewEntity(mc.player);

		for (int x = 0; x < 4; ++x) {
			for (int z = 0; z < 4; ++z) {
				world.doPreChunk(x - 2, z - 2, true);
			}
		}

		for (BlockPos p : BlockPos.getAllInBoxMutable(-8, 0, -8, 8, 1, 8))
			world.setBlockState(p, Blocks.BEDROCK.getDefaultState());

		for (BlockPos p : BlockPos.getAllInBoxMutable(-4, 2, -4, 4, 2, 4))
			world.setBlockState(p, Blocks.GLASS.getDefaultState());

		this.world.setBlockState(new BlockPos(0, 3, 0), Blocks.ENCHANTING_TABLE.getDefaultState());
		this.world.setBlockState(new BlockPos(0, 3, 2), Blocks.TORCH.getDefaultState());

		EntityPig e = new EntityPig(world);
		e.setPositionAndRotation(0, 7, 0, 0, 0);
		e.rotationYawHead = e.prevRotationYawHead = 60;
		e.renderYawOffset = e.prevRenderYawOffset = 30;
		world.spawnEntity(e);
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
		double x = -14;
		double y = 14;
		double z = 0;
		float yaw = -80;
		float pitch = 30;
		float roll = 20;
		float fovy = 45;
		float aspect = (float) mc.displayWidth / (float) mc.displayHeight;

		// setup root transform
		{
			GlStateManager.matrixMode(GL11.GL_PROJECTION);
			GlStateManager.loadIdentity();
			Project.gluPerspective(fovy, aspect, 0.05F, mc.gameSettings.renderDistanceChunks * 16 * MathHelper.SQRT_2);

			GlStateManager.matrixMode(GL11.GL_MODELVIEW);
			GlStateManager.loadIdentity();
			GlStateManager.rotate(roll, 0, 0, 1);
			GlStateManager.rotate(pitch, 1, 0, 0);
			GlStateManager.rotate(yaw + 180, 0, 1, 0);
		}

		// setup cache
		mc.player.setLocationAndAngles(x, y, z, yaw, pitch);
		ClippingHelperImpl.getInstance();
		ActiveRenderInfo.updateRenderInfo(mc.player, false);

		// update light map
		((ExtEntityRenderer) mc.entityRenderer).wolftail_forceUpdateLightmap(partialTicks);

		// draw sky
		{
			Vec3d skyColor = mc.world.getSkyColor(mc.player, partialTicks);
			GlStateManager.clearColor((float) skyColor.x, (float) skyColor.y, (float) skyColor.z, 0);
			GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);

			GlStateManager.matrixMode(GL11.GL_PROJECTION);
			GlStateManager.pushMatrix();
			GlStateManager.loadIdentity();
			Project.gluPerspective(fovy, aspect, 0.05F, mc.gameSettings.renderDistanceChunks * 16 * 2.0F);
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);

			rg.renderSky(partialTicks, 2);
			GlStateManager.disableAlpha();
			GlStateManager.disableFog();

			GlStateManager.matrixMode(GL11.GL_PROJECTION);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		}

		// build terrain
		ICamera icamera = new Frustum();
		icamera.setPosition(x, y, z);
		rg.setupTerrain(mc.player, partialTicks, icamera, frameCount++, false);
		rg.updateChunks(finishTimeNano);

		// draw terrain
		{
			ITextureObject tex_blocks = bindAndGetTexture(mc.renderEngine, TextureMap.LOCATION_BLOCKS_TEXTURE);
			GlStateManager.enableCull();

			GlStateManager.disableBlend();
			rg.renderBlockLayer(BlockRenderLayer.SOLID, partialTicks, 2, mc.player);

			GlStateManager.enableBlend();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

			tex_blocks.setBlurMipmap(false, mc.gameSettings.mipmapLevels > 0);
			rg.renderBlockLayer(BlockRenderLayer.CUTOUT_MIPPED, partialTicks, 2, mc.player);

			tex_blocks.restoreLastBlurMipmap();
			tex_blocks.setBlurMipmap(false, false);
			rg.renderBlockLayer(BlockRenderLayer.CUTOUT, partialTicks, 2, mc.player);

			tex_blocks.restoreLastBlurMipmap();
			rg.renderBlockLayer(BlockRenderLayer.TRANSLUCENT, partialTicks, 2, mc.player);
		}

		// draw entities
		GlStateManager.disableCull();
		RenderHelper.enableStandardItemLighting();
		ForgeHooksClient.setRenderPass(0);
		rg.renderEntities(mc.player, icamera, partialTicks);
		ForgeHooksClient.setRenderPass(1);
		rg.renderEntities(mc.player, icamera, partialTicks);
		ForgeHooksClient.setRenderPass(-1);

		// draw particles & rain snow
		RenderHelper.disableStandardItemLighting();
		mc.entityRenderer.enableLightmap();
		mc.effectRenderer.renderParticles(mc.player, partialTicks);
		mc.effectRenderer.renderLitParticles(mc.player, partialTicks);
		mc.entityRenderer.disableLightmap();
		((ExtEntityRenderer) mc.entityRenderer).wolftail_rendererUpdateCount_set(frameCount);
		((ExtEntityRenderer) mc.entityRenderer).wolftail_renderRainSnow(partialTicks);
	}

	private static ITextureObject bindAndGetTexture(TextureManager tm, ResourceLocation l) {
		tm.bindTexture(l);

		return tm.getTexture(l);
	}

	@Override
	public void handleChat(ChatType type, ITextComponent text) {
	}

	@Override
	public void handleLeave() {
		this.playContext = null;

		MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(this.world));
		this.world = Minecraft.getMinecraft().world = null;
		Minecraft.getMinecraft().player = null;
		Minecraft.getMinecraft().setRenderViewEntity(null);
	}

	@Override
	public void handle(ByteBuf buf) {
	}

	@Override
	public void tick() {
		this.world.setWorldTime(System.currentTimeMillis() % 24000);

		// Minecraft.getMinecraft().effectRenderer.updateEffects();
		// world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, -8, 8, 4, 0, 0, 0);

		world.rainingStrength = world.prevRainingStrength = 0.4f;

		while (Keyboard.next()) {
			if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
				this.playContext.disconnect();
			}
		}
	}
}
