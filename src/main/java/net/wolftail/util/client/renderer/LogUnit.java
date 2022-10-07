package net.wolftail.util.client.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.TextFormatting;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.internal.renderer.ExtRendererFontRenderer;

@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_CLIENT)
public final class LogUnit extends UIUnit {

	public static final int FONT_HEIGHT = 18;
	public static final int SCROLL_WIDTH = 24;

	private static final int INITIAL_CAPACITY = 512;

	private StringBuilder charBuf;
	private float scroll;

	private int cache_lineNum;
	private float cache_posX;

	public LogUnit(int width, int height) {
		super(width, height);

		this.charBuf = new StringBuilder(INITIAL_CAPACITY);
	}

	public LogUnit pPrint(boolean b) {
		return this.pPrint(String.valueOf(b));
	}

	public LogUnit pPrint(char c) {
		return this.pPrint(String.valueOf(c));
	}

	public LogUnit pPrint(int i) {
		return this.pPrint(String.valueOf(i));
	}

	public LogUnit pPrint(long l) {
		return this.pPrint(String.valueOf(l));
	}

	public LogUnit pPrint(float f) {
		return this.pPrint(String.valueOf(f));
	}

	public LogUnit pPrint(double d) {
		return this.pPrint(String.valueOf(d));
	}

	public LogUnit pPrint(TextFormatting f) {
		this.charBuf.append(f.toString());

		return this;
	}

	public LogUnit pPrint(Object o) {
		return this.pPrint(String.valueOf(o));
	}

	public LogUnit pPrint(CharSequence s) {
		if (s == null)
			s = "null";
		else if (s.length() == 0)
			return this;

		this.charBuf.append(s);
		this.updateCache(s);

		return this;
	}

	private void updateCache(CharSequence s) {
		ExtRendererFontRenderer fr = (ExtRendererFontRenderer) Minecraft.getMinecraft().fontRenderer;
		int vw = this.param_width - SCROLL_WIDTH;

		for (int i = 0, l = s.length(); i < l; ++i) {
			char cp = s.charAt(i);

			switch (cp) {
				case '\n':
					this.cache_lineNum++;
					this.cache_posX = 0;

					break;
				case '\u00a7':
					if (i + 1 < l) {
						i++;

						break;
					}
				default:
					if (this.cache_posX + fr.wolftail_widthOf(FONT_HEIGHT, cp) > vw) {
						this.cache_lineNum++;
						this.cache_posX = 0;
					}
			}
		}
	}

	public void pPrintln() {
		this.charBuf.append('\n');

		this.cache_lineNum++;
		this.cache_posX = 0;
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

	public void pPrintln(CharSequence s) {
		this.pPrint(s).pPrintln();
	}

	public void pClear() {
		if (this.charBuf.length() > INITIAL_CAPACITY * 1.75)
			this.charBuf = new StringBuilder(INITIAL_CAPACITY);
		else
			this.charBuf.setLength(0);

		this.scroll = 0;

		this.cache_lineNum = 0;
		this.cache_posX = 0;
	}

	public float pMaxScroll() {
		this.check();

		return Math.max((float) this.cache_lineNum - (float) this.param_height / (float) FONT_HEIGHT, 0);
	}

	public float pGetScroll() {
		this.check();

		return this.scroll;
	}

	public float pSetScroll(float s) {
		this.check();

		return this.scroll = (s <= 0 ? 0 : Math.min(s, this.pMaxScroll()));
	}

	public float pScrollMov(float ds) {
		return this.pSetScroll(this.scroll + ds);
	}

	public float pScrollEnd() {
		return this.scroll = this.pMaxScroll();
	}

	@Override
	void release0() {
		this.charBuf = null;
	}

	@Override
	void resize0(int oldWidth, int oldHeight) {
		if (this.param_width != oldWidth) {
			this.cache_lineNum = 0;
			this.cache_posX = 0;

			this.updateCache(this.charBuf);
		}

		this.pSetScroll(this.scroll);
	}

	@Override
	void flush0() {
		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_VIEWPORT_BIT | GL11.GL_TRANSFORM_BIT | GL11.GL_CURRENT_BIT
				| GL11.GL_ENABLE_BIT);

		this.bindAndExecute(() -> {
			StringBuilder buf = charBuf;
			int l = buf.length();

			FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
			ExtRendererFontRenderer fr_ext = (ExtRendererFontRenderer) fr;
			float vw = param_width;
			float vh = param_height;

			GL11.glViewport(0, 0, param_width, param_height);

			GL11.glClearColor(0, 0, 0, 0);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glOrtho(0, vw, vh, 0, -1, 1);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();

			float scroll = this.scroll;
			fr.posX = 0;
			fr.posY = -(scroll * FONT_HEIGHT);

			// draw scroll
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			drawRect(vw - SCROLL_WIDTH, 0, vw, vh, 0xD8FFFFFF);
			if (this.pMaxScroll() != 0)
				drawRect(vw - SCROLL_WIDTH, (vh * scroll) / (float) this.cache_lineNum, vw,
						vh * (vh + scroll * FONT_HEIGHT) / (float) (this.cache_lineNum * FONT_HEIGHT), 0x35000000);
			vw -= SCROLL_WIDTH;

			// enable to support rendering unicode
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.5F);
			GL11.glEnable(GL11.GL_TEXTURE_2D);

			// draw content
			Style style = new Style();
			setColor(fr, style);

			L: for (int i = 0; i < l; ++i) {
				char cp = buf.charAt(i);

				switch (cp) {
					case '\n':
						fr.posX = 0;
						fr.posY += FONT_HEIGHT;

						style.reset();
						setColor(fr, style);

						break;
					case '\u00a7':
						if (i + 1 < l) {
							style.update("0123456789abcdefklmnor".indexOf(lowerCaseEN(buf.charAt(++i))));
							setColor(fr, style);

							break;
						}
					default:
						if (fr.posX + fr_ext.wolftail_widthOf(FONT_HEIGHT, cp) > vw) {
							fr.posX = 0;
							fr.posY += FONT_HEIGHT;
						}

						if (fr.posY + FONT_HEIGHT < 0)
							break;
						if (fr.posY > vh)
							break L;

						if (style.randomStyle)
							cp = fr_ext.wolftail_randomReplacement(cp);

						float width = fr_ext.wolftail_renderCodepoint(FONT_HEIGHT, cp, style.italicStyle);

						if (style.boldStyle) {
							fr.posX += 2;
							fr_ext.wolftail_renderCodepoint(FONT_HEIGHT, cp, style.italicStyle);
							fr.posX -= 2;

							width += 2;
						}

						fr_ext.wolftail_renderAttachment(width, style.strikethroughStyle, style.underlineStyle);

						fr.posX += width;
				}
			}

			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glPopMatrix();
		});

		GL11.glPopAttrib();
	}

	private static char lowerCaseEN(char cp) {
		return 'A' <= cp && cp <= 'Z' ? (char) (cp + ('a' - 'A')) : cp;
	}

	private static void setColor(FontRenderer fr, Style s) {
		int color = fr.colorCode[s.colorCode];

		GL11.glColor4f((float) (color >> 16) / 255.0F, (float) (color >> 8 & 255) / 255.0F,
				(float) (color & 255) / 255.0F, 1.0F);
	}

	private static void drawRect(float left, float top, float right, float bottom, int color) {
		GL11.glColor4f((float) (color >> 16 & 255) / 255.0F, (float) (color >> 8 & 255) / 255.0F,
				(float) (color & 255) / 255.0F, (float) (color >> 24 & 255) / 255.0F);

		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buffer = tess.getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		buffer.pos(left, bottom, 0).endVertex();
		buffer.pos(right, bottom, 0).endVertex();
		buffer.pos(right, top, 0).endVertex();
		buffer.pos(left, top, 0).endVertex();
		tess.draw();
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
			switch (op) {
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
					if (op < 16) {
						if (op < 0)
							op = 15;

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