package com.example.examplemod;

import org.lwjgl.input.Keyboard;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.wolftail.api.INetworkHandler;
import net.wolftail.api.PlayContext;
import net.wolftail.util.client.renderer.VanillaClientHandler;
import net.wolftail.util.client.renderer.VanillaUpdater;

public final class PigClientHandler extends VanillaClientHandler implements INetworkHandler {

	static final PigClientHandler INSTANCE = new PigClientHandler();

	private PlayContext playContext;

	EntityPig e;

	private PigClientHandler() {
	}

	@Override
	protected void handleEnter0(PlayContext context) {
		this.playContext = context;
		context.setHandler(this);

		VanillaUpdater u = this.getUpdater();

		u.setCamera(-4, 4, 0, -80, 0, 0, 45);

		for (BlockPos p : BlockPos.getAllInBoxMutable(-8, 0, -8, 8, 1, 8))
			u.setBlockState(p, Blocks.BEDROCK.getDefaultState());

		for (BlockPos p : BlockPos.getAllInBoxMutable(-4, 2, -4, 4, 2, 4))
			u.setBlockState(p, Blocks.GLASS.getDefaultState());

		u.setBlockState(new BlockPos(0, 3, 0), Blocks.ENCHANTING_TABLE.getDefaultState());
		u.setBlockState(new BlockPos(0, 3, 2), Blocks.TORCH.getDefaultState());
		u.setBlockState(new BlockPos(1, 3, 1), Blocks.ENDER_CHEST.getDefaultState());
		u.setBlockState(new BlockPos(0, 3, -2), Blocks.BOOKSHELF.getDefaultState());
		u.setBlockState(new BlockPos(0, 4, -2), Blocks.BOOKSHELF.getDefaultState());
		u.setBlockState(new BlockPos(-1, 3, -2), Blocks.BOOKSHELF.getDefaultState());
		u.setBlockState(new BlockPos(-6, 2, 3), Blocks.BREWING_STAND.getDefaultState());

		e = new EntityPig(Minecraft.getMinecraft().world);
		e.setPositionAndRotation(1, 4.5, 0, 0, 0);
		Minecraft.getMinecraft().world.spawnEntity(e);
	}

	@Override
	protected void handleFrame0() {

	}

	@Override
	protected void handleTick0() {
		this.getUpdater().setTime((int) (System.currentTimeMillis() % 24000));

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
	public void handleChat(ChatType type, ITextComponent text) {
	}

	@Override
	public void handle(ByteBuf buf) {
	}
}
