package net.wolftail.util.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Preconditions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public final class CmdUnit extends UIUnit {
	
	private int arg_ppf;
	
	private List<Queue<Object>> arg_lines;
	
	public CmdUnit(int width, int height, int ppf) {
		super(width, height, true, false);
		
		this.usePPF(ppf);
		
		this.arg_lines = new ArrayList<>();
		this.lf();
	}
	
	public void usePPF(int ppf) {
		Preconditions.checkArgument(ppf > 0);
		
		this.arg_ppf = ppf;
	}
	
	public int ppfInUse() {
		return this.arg_ppf;
	}
	
	public void appendSegment(Object segment) {
		this.arg_lines.get(this.arg_lines.size() - 1).add(segment);
	}
	
	public void lf() {
		this.arg_lines.add(new LinkedList<>());
	}
	
	public void cls() {
		this.arg_lines.clear();
	}
	
	@Override
	void flush0() {
		GL11.glClearColor(0, 0, 0, 1);
		GL11.glClearDepth(1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, this.param_width, this.param_height, 0, -1, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		
		FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;
		Iterator<Queue<Object>> iter = this.arg_lines.iterator();
		
		int ppf = this.arg_ppf, h = renderer.FONT_HEIGHT, y = 0;
		
		GL11.glTranslatef(0, 0, 0);
		glScalef((float) ppf / (float) renderer.FONT_HEIGHT);
		
		StringBuilder buf = new StringBuilder();
		
		while(iter.hasNext()) {
			renderer.drawString(bakeLine(buf, iter.next()), 0, y, -1);
			
			y += h;
		}
	}
	
	private static final void glScalef(float s) {
		GL11.glScaled(s, s, s);
	}
	
	private static final String bakeLine(StringBuilder buf, Queue<Object> raw) {
		buf.setLength(0);
		
		for(Object elem : raw)
			buf.append(elem);
		
		return buf.toString();
	}
	
	@Override
	public UnitType type() {
		return UnitType.CMD;
	}
}
