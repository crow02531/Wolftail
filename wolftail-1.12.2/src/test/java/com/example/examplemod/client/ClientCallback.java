package com.example.examplemod.client;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.wolftail.api.ClientPlayContext;
import net.wolftail.api.IClientEntryPoint;
import net.wolftail.api.IClientFrameCallback;
import net.wolftail.util.client.renderer.CmdUnit;

public class ClientCallback implements IClientEntryPoint, IClientFrameCallback {
	
	static CmdUnit ui;
	
	@Override
	public void onFrame(ClientPlayContext context) {
		CmdUnit cmd = ui;
		
		if(Display.wasResized()) {
			Minecraft mc = Minecraft.getMinecraft();
			
			cmd.resize(mc.displayWidth >> 1, mc.displayHeight >> 1);
		}
		
		GlStateManager.clearColor(0, 0, 0, 1);
		GlStateManager.clearDepth(1);
		GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0, 1, 1, 0, -1, 1);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.loadIdentity();
		
		cmd.flush();
		cmd.render(new Vector3f(0, 1, 0), new Vector3f(1, 1, 0), new Vector3f(1, 0, 0), new Vector3f(0, 0, 0));
	}
	
	@Override
	public void onEnter(ClientPlayContext context) {
		context.setNetHandler(new ClientNetHandler(context));
		
		ui = new CmdUnit(Minecraft.getMinecraft().displayWidth >> 1, Minecraft.getMinecraft().displayHeight >> 1);
	}
}
