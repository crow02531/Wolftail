package com.example.examplemod;

import org.lwjgl.input.Keyboard;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.wolftail.api.INetworkHandler;
import net.wolftail.api.PlayContext;
import net.wolftail.util.MoreBlockPos;
import net.wolftail.util.MoreByteBufs;
import net.wolftail.util.client.renderer.VanillaClientHandler;
import net.wolftail.util.tracker.ContentDiff;
import net.wolftail.util.tracker.DiffVisitor;
import net.wolftail.util.tracker.builtin.TraceVisitor;

public final class PigClientHandler extends VanillaClientHandler implements INetworkHandler, DiffVisitor {

	static final PigClientHandler INSTANCE = new PigClientHandler();

	private PlayContext playContext;

	private PigClientHandler() {
	}

	@Override
	protected void handleEnter0(PlayContext context) {
		this.playContext = context;
		context.setHandler(this);

		this.setCamera(10, 40, -20, 0, 40, 0, 45);
	}

	@Override
	protected void handleFrame0() {
		this.setCamera(10, 20, -20, 0, 40, 0, 45);
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
		buf.retain();

		Minecraft.getMinecraft().addScheduledTask(() -> {
			ContentDiff.apply(buf, new TraceVisitor(System.out, this));
			buf.release();
		});
	}

	private int chunkX, chunkZ;
	private BlockPos blockPos;

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

		for (int x = -1; x <= 1; ++x) {
			for (int z = -1; z <= 1; ++z) {
				if (!cp.isChunkGeneratedAt(chunkX + x, chunkZ + z))
					cp.loadChunk(chunkX + x, chunkZ + z);
			}
		}
	}

	@Override
	public void jzBindBlock(short index) {
		this.blockPos = MoreBlockPos.toPos(chunkX, chunkZ, index);
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
		WorldClient w = getWorld();
		ExtendedBlockStorage s = buf == null ? null : new ExtendedBlockStorage(index, true);

		if (buf != null)
			s.getData().read(MoreByteBufs.wrap(buf));

		for (int x = 0; x < 16; ++x) {
			for (int y = 0; y < 16; ++y) {
				for (int z = 0; z < 16; ++z) {
					w.setBlockState(new BlockPos(x + chunkX * 16, y + index * 16, z + chunkZ * 16),
							s == null ? Blocks.AIR.getDefaultState() : s.get(x, y, z));
				}
			}
		}
	}

	@Override
	public void jzSetState(IBlockState state) {
		getWorld().setBlockState(blockPos, state);
	}

	@Override
	public void jzSetTileEntity(ByteBuf buf) {
	}
}
