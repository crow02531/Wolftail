package com.example.examplemod;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.wolftail.api.IClientHandler;
import net.wolftail.api.INetworkHandler;
import net.wolftail.api.PlayContext;
import net.wolftail.util.client.renderer.VanillaUnit;

public final class PigClientHandler implements IClientHandler, INetworkHandler {

	static final PigClientHandler INSTANCE = new PigClientHandler();

	private PlayContext playContext;

	private VanillaUnit ui;

	private PigClientHandler() {
	}

	@Override
	public void handleEnter(PlayContext context) {
		this.playContext = context;
		context.setHandler(this);

		ui = new VanillaUnit(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);

		for (BlockPos p : BlockPos.getAllInBoxMutable(-8, 0, -8, 8, 1, 8))
			ui.pSetState(p, Blocks.BEDROCK.getDefaultState());

		for (BlockPos p : BlockPos.getAllInBoxMutable(-4, 2, -4, 4, 2, 4))
			ui.pSetState(p, Blocks.GLASS.getDefaultState());

		ui.pSetState(new BlockPos(0, 3, 0), Blocks.ENCHANTING_TABLE.getDefaultState());
		ui.pSetState(new BlockPos(0, 3, 2), Blocks.TORCH.getDefaultState());

		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0, 1, 1, 0, -1, 1);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.loadIdentity();
		GlStateManager.disableDepth();
	}

	@Override
	public void handleFrame() {
		if (Display.wasResized())
			ui.resize(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);

		ui.pCamera(-20, 14, 0, -80, 30, 30, 45);
		ui.flush();

		ui.render(new Vector3f(0, 1, 0), new Vector3f(1, 1, 0), new Vector3f(1, 0, 0), new Vector3f(0, 0, 0));
	}

	@Override
	public void handleChat(ChatType type, ITextComponent text) {
	}

	@Override
	public void handleLeave() {
		this.playContext = null;

		this.ui.release();
	}

	@Override
	public void handle(ByteBuf buf) {
	}

	@Override
	public void tick() {
		ui.pTime((int) (System.currentTimeMillis() % 24000));

		while (Keyboard.next()) {
			if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
				this.playContext.disconnect();
			}
		}
	}
}
