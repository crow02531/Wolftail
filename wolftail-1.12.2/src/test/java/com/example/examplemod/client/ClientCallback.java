package com.example.examplemod.client;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import net.minecraft.client.Minecraft;
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
			
			cmd.resize(mc.displayWidth, mc.displayHeight);
		}
		
		GL11.glClearColor(0, 0, 0, 1);
		GL11.glClearDepth(1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, 1, 1, 0, -1, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		
		cmd.flush();
		cmd.render(new Vector3f(0, 1, 0), new Vector3f(1, 1, 0), new Vector3f(1, 0, 0), new Vector3f(0, 0, 0));
	}
	
	@Override
	public void onEnter(ClientPlayContext context) {
		context.setNetHandler(new ClientNetHandler(context));
		
		//TODO get the actual PPI of screen. Toolkit's is fault.
		ui = new CmdUnit(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
		ui.usePPU(21);
	}
}
