package net.wolftail.util.client;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Preconditions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;

//TODO horizontal scroll bar
public final class CmdUnit extends UIUnit {
	
	private String arg_doc;
	private int arg_ppu;
	
	private int arg_scrollV;
	
	private int tmp_realH;
	
	public CmdUnit(int width, int height, int ppu) {
		super(width, height, true, false);
		
		this.usePPU(ppu);
		
		this.tmp_realH = -1;
	}
	
	public void usePPU(int ppu) {
		Preconditions.checkArgument(ppu > 0);
		
		int old = this.arg_ppu;
		this.arg_ppu = ppu;
		
		if(this.tmp_realH > 0)
			this.tmp_realH = (this.tmp_realH / old) * ppu; //divisible
		
		this.setScrollVertical(this.arg_scrollV);
	}
	
	public int ppuInUse() {
		return this.arg_ppu;
	}
	
	public void useDoc(String doc) {
		this.arg_doc = doc;
		
		this.tmp_realH = -1;
		this.setScrollVertical(this.arg_scrollV);
	}
	
	public String docInUse() {
		return this.arg_doc;
	}
	
	public void setScrollVertical(int sv) {
		if(sv <= 0) {
			this.arg_scrollV = 0;
			
			return;
		}
		
		if(this.tmp_realH < 0)
			this.tmp_realH = countLF(this.arg_doc) * this.arg_ppu;
		
		this.arg_scrollV = MathHelper.clamp(sv, 0, this.tmp_realH - this.param_height);
	}
	
	public int getScrollVertical() {
		return this.arg_scrollV;
	}
	
	void resize0() {
		this.setScrollVertical(this.arg_scrollV);
	}
	
	@Override
	void flush0() {
		GL11.glClearColor(0, 0, 0, 1);
		GL11.glClearDepth(1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		String doc = this.arg_doc;
		
		if(doc == null || doc.isEmpty()) return;
		
		String[] lines = doc.split("\n");
		FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;
		
		int ppu = this.arg_ppu, fh = renderer.FONT_HEIGHT;
		float scale = (float) ppu / (float) fh;
		float vw = this.param_width, vh = this.param_height;
		float rh = this.tmp_realH = lines.length * ppu;
		float sv = this.arg_scrollV;
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, vw, vh, 0, -1, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		
		//vertical scroll bar
		if(rh > vh) {
			drawRect(vw - ppu, 0, vw, vh, 0xD8FFFFFF); //background
			drawRect(vw - ppu, (vh * sv) / rh, vw, vh * (vh + sv) / rh, 0x35000000); //button
		}
		
		GL11.glTranslatef(0, -sv, 0);
		GL11.glScalef(scale, scale, scale);
		
		for(int i = 0; i < lines.length; ++i)
			renderer.drawString(lines[i], 0, i * fh, 0xFFFFFFFF);
	}
	
	private static int countLF(String text) {
		int num = 0;
		
		for(int i = 0, l = text.length(); i < l; ++i) {
			if(text.charAt(i) == '\n')
				num++;
		}
		
		return num;
	}
	
	private static void drawRect(float left, float top, float right, float bottom, int color) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color((float) (color >> 16 & 255) / 255.0F, (float) (color >> 8 & 255) / 255.0F, (float) (color & 255) / 255.0F, (float) (color >> 24 & 255) / 255.0F);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
		bufferbuilder.pos(left, bottom, 0).endVertex();
		bufferbuilder.pos(right, bottom, 0).endVertex();
		bufferbuilder.pos(right, top, 0).endVertex();
		bufferbuilder.pos(left, top, 0).endVertex();
		tessellator.draw();
		GlStateManager.disableBlend();
	}
	
	@Override
	public UnitType type() {
		return UnitType.CMD;
	}
}
