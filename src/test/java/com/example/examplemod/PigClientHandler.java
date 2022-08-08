package com.example.examplemod;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import net.wolftail.api.IClientHandler;
import net.wolftail.api.INetworkHandler;
import net.wolftail.api.PlayContext;
import net.wolftail.util.client.renderer.CmdUnit;

public final class PigClientHandler implements IClientHandler, INetworkHandler {
	
	static final PigClientHandler INSTANCE = new PigClientHandler();
	
	private PlayContext playContext;
	private CmdUnit ui;
	
	private PigClientHandler() {
	}
	
	@Override
	public void handleEnter(PlayContext context) {
		this.playContext = context;
		context.setHandler(this);
		
		this.ui = new CmdUnit(calcWidth(), calcHeight());
	}
	
	@Override
	public void handleFrame() {
		if (Display.wasResized())
			this.ui.resize(calcWidth(), calcHeight());
		
		GlStateManager.clearColor(0, 0, 0, 1);
		GlStateManager.clearDepth(1);
		GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0, 1, 1, 0, -1, 1);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.loadIdentity();
		
		this.ui.flush();
		this.ui.render(new Vector3f(0, 1, 0), new Vector3f(1, 1, 0), new Vector3f(1, 0, 0), new Vector3f(0, 0, 0));
	}
	
	@Override
	public void handleLeave() {
		this.ui.release();
		this.ui = null;
		this.playContext = null;
	}
	
	@Override
	public void handle(ByteBuf buf) {
		
	}
	
	@Override
	public void tick() {
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				switch (Keyboard.getEventKey()) {
				case Keyboard.KEY_ESCAPE:
					this.playContext.disconnect();
					
					break;
				case Keyboard.KEY_RETURN:
					this.ui.pPrintln();
					
					break;
				case Keyboard.KEY_LCONTROL:
					this.ui.pPrint('\u00a7');
					
					break;
				case Keyboard.KEY_RCONTROL:
					this.ui.pPrint('\r');
					
					break;
				default:
					char c = Keyboard.getEventCharacter();
					
					if (ChatAllowedCharacters.isAllowedCharacter(c))
						this.ui.pPrint(c);
				}
			}
		}
	}
	
	private static int calcHeight() {
		return Minecraft.getMinecraft().displayHeight >> 1;
	}
	
	private static int calcWidth() {
		return Minecraft.getMinecraft().displayWidth >> 1;
	}
}
