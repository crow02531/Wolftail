package net.wolftail.util.client.renderer;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.impl.ExtensionsFontRenderer;
import net.wolftail.impl.SharedImpls;

@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_CLIENT)
public final class CmdUnit extends UIUnit {
	
	private StringBuilder charBuf;
	private float scroll;
	
	public CmdUnit(int width, int height) {
		super(width, height, true, false);
		
		this.charBuf = new StringBuilder(512);
	}
	
	public CmdUnit pPrint(boolean b) {
		this.charBuf.append(b);
		
		return this;
	}
	
	public CmdUnit pPrint(char c) {
		this.charBuf.append(c);
		
		return this;
	}
	
	public CmdUnit pPrint(int i) {
		this.charBuf.append(i);
		
		return this;
	}
	
	public CmdUnit pPrint(long l) {
		this.charBuf.append(l);
		
		return this;
	}
	
	public CmdUnit pPrint(float f) {
		this.charBuf.append(f);
		
		return this;
	}
	
	public CmdUnit pPrint(double d) {
		this.charBuf.append(d);
		
		return this;
	}
	
	public CmdUnit pPrint(TextFormatting f) {
		this.charBuf.append(f.toString());
		
		return this;
	}
	
	public CmdUnit pPrint(Object o) {
		this.charBuf.append(o);
		
		return this;
	}
	
	public CmdUnit pPrint(String s) {
		this.charBuf.append(s);
		
		return this;
	}
	
	public void pPrintln() {
		this.charBuf.append('\n');
	}
	
	public void pPrintln(boolean b) {
		this.pPrint(b).pPrintln();
	}
	
	public void pPrintln(char c) {
		this.pPrint(c).pPrintln();
	}
	
	public void pPrintln(int i) {
		this.pPrint(i).pPrintln();
	}
	
	public void pPrintln(long l) {
		this.pPrint(l).pPrintln();
	}
	
	public void pPrintln(float f) {
		this.pPrint(f).pPrintln();
	}
	
	public void pPrintln(double d) {
		this.pPrint(d).pPrintln();
	}
	
	public void pPrintln(Object o) {
		this.pPrint(o).pPrintln();
	}
	
	public void pPrintln(String s) {
		this.pPrint(s).pPrintln();
	}
	
	public void pClear() {
		this.charBuf = new StringBuilder(512);
		this.scroll = 0;
	}
	
	public float pSetScroll(float s) {
		return 0; //TODO feature: scroll
	}
	
	public float pGetScroll() {
		return this.scroll;
	}
	
	@Nonnull
	@Override
	public UnitType type() {
		return UnitType.CMD;
	}
	
	void release0() {
		this.charBuf = null;
	}
	
	void resize0() {
		
	}
	
	@Override
	void flush0() {
		StringBuilder buf = this.charBuf;
		if(buf.length() == 0) return;
		
		float vw = this.param_width;
		float vh = this.param_height;
		
		GlStateManager.clearColor(0, 0, 0, 0);
		GlStateManager.clearDepth(1);
		GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0, vw, vh, 0, -1, 1);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.loadIdentity();
		
		ExtensionsFontRenderer fr = SharedImpls.as(Minecraft.getMinecraft().fontRenderer);
		
		fr.wolftail_posX_set(0);
		fr.wolftail_posY_set(0);
		
		Style style = new Style();
		setColor(fr, style);
		
		for(int i = 0, l = buf.length(); i < l; ++i) {
			int cp = buf.charAt(i);
			
			switch(cp) {
			case '\n':
				fr.wolftail_posX_set(0);
				fr.wolftail_posY_add(9);
				
				style.reset();
				setColor(fr, style);
				
				break;
			case '\u00a7':
				if(i + 1 < l) {
					style.update("0123456789abcdefklmnor".indexOf(lowerCaseEN(buf.charAt(++i))));
					setColor(fr, style);
					
					break;
				}
			default:
				if(fr.wolftail_posX_get() + fr.wolftail_widthOf(cp) > vw) {
					fr.wolftail_posX_set(0);
					fr.wolftail_posY_add(9);
				}
				
				if(fr.wolftail_posY_get() + 9 < 0 || fr.wolftail_posY_get() > vh)
					break;
				
				if(style.randomStyle) cp = fr.wolftail_randomReplacement(cp);
				
				float width = fr.wolftail_renderCodepoint(cp, style.italicStyle);
				
				if(style.boldStyle) {
					fr.wolftail_posX_add(1);
					fr.wolftail_renderCodepoint(cp, style.italicStyle);
					fr.wolftail_posX_add(-1);
					
					width += 1;
				}
				
				fr.wolftail_renderAttachment(width, style.strikethroughStyle, style.underlineStyle);
				
				fr.wolftail_posX_add(width);
			}
		}
	}
	
	private static int lowerCaseEN(int codepoint) {
		return 'A' <= codepoint && codepoint <= 'Z' ? codepoint + ('a' - 'A') : codepoint;
	}
	
	private static void setColor(ExtensionsFontRenderer fr, Style s) {
		int color = fr.wolftail_codeToColor(s.colorCode);
		
		GlStateManager.color((float) (color >> 16) / 255.0F, (float) (color >> 8 & 255) / 255.0F, (float) (color & 255) / 255.0F, 1.0F);
	}
	
	private static final class Style {
		
		private boolean randomStyle;
		private boolean boldStyle;
		private boolean strikethroughStyle;
		private boolean underlineStyle;
		private boolean italicStyle;
		
		private int colorCode;
		
		{
			this.reset();
		}
		
		private void reset() {
			this.update(-1);
		}
		
		private void update(int op) {
			switch(op) {
			case 16:
				this.randomStyle = true;
				
				break;
			case 17:
				this.boldStyle = true;
				
				break;
			case 18:
				this.strikethroughStyle = true;
				
				break;
			case 19:
				this.underlineStyle = true;
				
				break;
			case 20:
				this.italicStyle = true;
				
				break;
			case 21:
				this.randomStyle = false;
				this.boldStyle = false;
				this.strikethroughStyle = false;
				this.underlineStyle = false;
				this.italicStyle = false;
				
				break;
			default:
				if(op < 16) {
					if(op < 0) op = 15;
					
					this.randomStyle = false;
					this.boldStyle = false;
					this.strikethroughStyle = false;
					this.underlineStyle = false;
					this.italicStyle = false;
					
					this.colorCode = op;
				}
			}
		}
	}
}