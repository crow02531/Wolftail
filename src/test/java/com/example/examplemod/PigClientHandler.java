package com.example.examplemod;

import org.lwjgl.util.glu.Project;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.wolftail.api.IClientHandler;
import net.wolftail.api.INetworkHandler;
import net.wolftail.api.PlayContext;

public final class PigClientHandler implements IClientHandler, INetworkHandler {

	static final PigClientHandler INSTANCE = new PigClientHandler();

	private PlayContext playContext;
	private WorldClient world;

	private PigClientHandler() {
	}

	@Override
	public void handleEnter(PlayContext context) {
		this.playContext = context;
		context.setHandler(this);

		Minecraft mc = Minecraft.getMinecraft();

		this.world = mc.world = new WorldClient(null, new WorldSettings(0, null, false, false, WorldType.DEFAULT), 0, null, mc.mcProfiler);
		mc.renderGlobal.setWorldAndLoadRenderers(this.world);
		mc.effectRenderer.clearEffects(this.world);
		TileEntityRendererDispatcher.instance.setWorld(this.world);
		MinecraftForgeClient.clearRenderCache();

		for (int x = -1; x <= 1; ++x)
			for (int z = -1; z <= 1; ++z) {
				this.world.doPreChunk(x, z, true);
				Chunk c = this.world.getChunkFromChunkCoords(x, z);

				for (BlockPos p : BlockPos.getAllInBoxMutable(0, 0, 0, 16, 0, 16))
					c.setBlockState(p, Blocks.BEDROCK.getDefaultState());
			}
	}

	@Override
	public void handleFrame() {
		Minecraft mc = Minecraft.getMinecraft();

		int j = Math.min(Minecraft.getDebugFPS(), mc.gameSettings.limitFramerate);
		j = Math.max(j, 60);
		long l = Math.max((long) (1000000000 / j / 4), 0L);

		GlStateManager.viewport(0, 0, mc.displayWidth, mc.displayHeight);
		GlStateManager.clear(16640);

		renderWorld(mc, mc.renderGlobal, mc.getRenderPartialTicks(), System.nanoTime() + l);
	}

	private static int frameCount;

	private static void renderWorld(Minecraft mc, RenderGlobal rg, float partialTicks, long finishTimeNano) {
		GlStateManager.enableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(516, 0.5F);
		GlStateManager.enableCull();
		GlStateManager.shadeModel(7425);
		ClippingHelperImpl.getInstance(); // trigger init

		double x = 0;
		double y = 5;
		double z = 0;
		double yaw = 0;
		double pitch = 0;
		double roll = 0;

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
			GlStateManager.rotate((float) yaw, 0.0F, 1.0F, 0.0F);
		}

		ICamera icamera = new Frustum();
		icamera.setPosition(x, y, z);
		Entity fakeViewEntity = makeFake(x, y, z, yaw, pitch);

		mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		RenderHelper.disableStandardItemLighting();

		rg.setupTerrain(fakeViewEntity, partialTicks, icamera, frameCount++, true);
		rg.updateChunks(finishTimeNano);

		GlStateManager.matrixMode(5888);
		GlStateManager.pushMatrix();

		// draw terrain 1
		GlStateManager.disableAlpha();
		rg.renderBlockLayer(BlockRenderLayer.SOLID, partialTicks, 2, fakeViewEntity);
		GlStateManager.enableAlpha();
		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false,
				mc.gameSettings.mipmapLevels > 0);
		rg.renderBlockLayer(BlockRenderLayer.CUTOUT_MIPPED, partialTicks, 2, fakeViewEntity);
		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
		rg.renderBlockLayer(BlockRenderLayer.CUTOUT, partialTicks, 2, fakeViewEntity);
		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();

		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();

		GlStateManager.shadeModel(7424);
		GlStateManager.alphaFunc(516, 0.1F);

		// draw block breaking progress
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE,
				GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
		rg.drawBlockDamageTexture(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), fakeViewEntity,
				partialTicks);
		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();

		GlStateManager.depthMask(false);
		GlStateManager.shadeModel(7425);

		// draw terrain 2
		mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		rg.renderBlockLayer(BlockRenderLayer.TRANSLUCENT, partialTicks, 2, fakeViewEntity);
	}

	private static Entity makeFake(double x, double y, double z, double yaw, double pitch) {
		Entity e = new Entity(null) {

			@Override
			protected void entityInit() {
			}

			@Override
			protected void readEntityFromNBT(NBTTagCompound compound) {
			}

			@Override
			protected void writeEntityToNBT(NBTTagCompound compound) {
			}
		};

		e.posX = x;
		e.posY = y;
		e.posZ = z;
		e.rotationYaw = (float) yaw;
		e.rotationPitch = (float) pitch;

		return e;
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

	}
}
