package com.example.examplemod;

import org.lwjgl.input.Keyboard;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.DimensionType;
import net.wolftail.api.INetworkHandler;
import net.wolftail.api.PlayContext;
import net.wolftail.util.client.renderer.VanillaClientHandler;
import net.wolftail.util.tracker.ContentDiff;
import net.wolftail.util.tracker.DiffVisitor;

public final class PigClientHandler extends VanillaClientHandler implements INetworkHandler, DiffVisitor {

	static final PigClientHandler INSTANCE = new PigClientHandler();

	private PlayContext playContext;

	EntityPig e;

	private PigClientHandler() {
	}

	@Override
	protected void handleEnter0(PlayContext context) {
		this.playContext = context;
		context.setHandler(this);

		WorldClient w = getWorld();

		for (int x = 0; x < 4; ++x) {
			for (int z = 0; z < 4; ++z) {
				w.doPreChunk(x - 2, z - 2, true);
			}
		}

		this.setCamera(-4, 4, 0, -80, 0, 0, 45);

		for (BlockPos p : BlockPos.getAllInBoxMutable(-8, 0, -8, 8, 1, 8))
			w.setBlockState(p, Blocks.BEDROCK.getDefaultState());

		for (BlockPos p : BlockPos.getAllInBoxMutable(-4, 2, -4, 4, 2, 4))
			w.setBlockState(p, Blocks.GLASS.getDefaultState());

		w.setBlockState(new BlockPos(0, 3, 0), Blocks.ENCHANTING_TABLE.getDefaultState());
		w.setBlockState(new BlockPos(0, 3, 2), Blocks.TORCH.getDefaultState());
		w.setBlockState(new BlockPos(1, 3, 1), Blocks.ENDER_CHEST.getDefaultState());
		w.setBlockState(new BlockPos(0, 3, -2), Blocks.BOOKSHELF.getDefaultState());
		w.setBlockState(new BlockPos(0, 4, -2), Blocks.BOOKSHELF.getDefaultState());
		w.setBlockState(new BlockPos(-6, 2, 3), Blocks.BREWING_STAND.getDefaultState());
		w.setBlockState(new BlockPos(-1, 3, -2), Blocks.STONE.getDefaultState());

		w.sendBlockBreakProgress(0, new BlockPos(-1, 3, -2), 6);

		e = new EntityPig(w);
		e.setPositionAndRotation(1, 4.5, 0, 0, 0);
		w.spawnEntity(e);
	}

	@Override
	protected void handleFrame0() {

	}

	@Override
	protected void handleTick0() {
		while (Keyboard.next()) {
			if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
				this.playContext.disconnect();
			}
		}
	}

	@Override
	protected void handleLeave0() {
		this.playContext = null;
	}

	@Override
	protected void handleChat0(ChatType type, ITextComponent text) {
	}

	@Override
	public void handle(ByteBuf buf) {
		ContentDiff.apply(buf, this);
	}

	private int chunkX, chunkZ;
	private BlockPos localPos;

	@Override
	public void jzBegin() {
	}

	@Override
	public void jzEnd() {
	}

	@Override
	public void jzBindWorld(DimensionType dim) {
	}

	@Override
	public void jzBindChunk(int chunkX, int chunkZ) {
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;

		ChunkProviderClient cp = getWorld().getChunkProvider();

		if(!cp.isChunkGeneratedAt(chunkX, chunkZ))
			cp.loadChunk(chunkX, chunkZ);
	}

	@Override
	public void jzBindBlock(short index) {
		this.localPos = new BlockPos(index >> 12 & 0xF, index & 0xFF, index >> 8 & 0xF);
	}

	@Override
	public void jzUnbindWorld() {
	}

	@Override
	public void jzUnbindChunk() {
	}

	@Override
	public void jzUnbindBlock() {
	}

	@Override
	public void jzSetDaytime(int daytime) {
		getWorld().setWorldTime(daytime);
	}

	@Override
	public void jzSetWeather(float rainStr, float thunderStr) {
		WorldClient w = getWorld();

		w.rainingStrength = rainStr;
		w.thunderingStrength = thunderStr;
	}

	@Override
	public void jzSetSection(int index, ByteBuf buf) {

	}

	@Override
	public void jzSetState(IBlockState state) {

	}

	@Override
	public void jzSetTileEntity(ByteBuf buf) {

	}
}
