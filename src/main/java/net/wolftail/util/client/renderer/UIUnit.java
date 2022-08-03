package net.wolftail.util.client.renderer;

import java.nio.IntBuffer;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

import com.google.common.base.Preconditions;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;

@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_CLIENT)
public abstract class UIUnit {
	
	static {
		if (!OpenGlHelper.isFramebufferEnabled())
			throw new IllegalStateException("Framebuffer unsupported! Please check your OpenGL.");
	}
	
	int param_width;
	int param_height;
	
	boolean param_depth;
	boolean param_stencil;
	
	boolean state_dirty;
	
	private int object_fb;
	private int object_cb;
	
	private int object_rb;
	
	private void create() {
		int width = this.param_width, height = this.param_height;
		
		int fbo = this.object_fb = OpenGlHelper.glGenFramebuffers();
		int cbo = this.object_cb = TextureUtil.glGenTextures();
		
		int old_fb = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
		int old_te = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
		
		OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, fbo);
		
		GlStateManager.bindTexture(cbo);
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		GlStateManager.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA,
				GL11.GL_UNSIGNED_BYTE, (IntBuffer) null);
		OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0,
				GL11.GL_TEXTURE_2D, cbo, 0);
		GlStateManager.bindTexture(old_te);
		
		if (this.param_depth) {
			int old_rb = GL11.glGetInteger(GL30.GL_RENDERBUFFER_BINDING);
			int rbo = this.object_rb = OpenGlHelper.glGenRenderbuffers();
			
			OpenGlHelper.glBindRenderbuffer(OpenGlHelper.GL_RENDERBUFFER, rbo);
			
			int x, y;
			
			if (this.param_stencil) {
				x = GL30.GL_DEPTH24_STENCIL8;
				y = GL30.GL_DEPTH_STENCIL_ATTACHMENT;
			} else {
				x = GL14.GL_DEPTH_COMPONENT24;
				y = GL30.GL_DEPTH_ATTACHMENT;
			}
			
			OpenGlHelper.glRenderbufferStorage(OpenGlHelper.GL_RENDERBUFFER, x, width, height);
			OpenGlHelper.glFramebufferRenderbuffer(OpenGlHelper.GL_FRAMEBUFFER, y, OpenGlHelper.GL_RENDERBUFFER, rbo);
			
			OpenGlHelper.glBindRenderbuffer(OpenGlHelper.GL_RENDERBUFFER, old_rb);
		}
		
		check0();
		
		OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, old_fb);
	}
	
	private static void check0() {
		int i = OpenGlHelper.glCheckFramebufferStatus(OpenGlHelper.GL_FRAMEBUFFER);
		
		if (i != OpenGlHelper.GL_FRAMEBUFFER_COMPLETE) {
			if (i == OpenGlHelper.GL_FB_INCOMPLETE_ATTACHMENT)
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
			else if (i == OpenGlHelper.GL_FB_INCOMPLETE_MISS_ATTACH)
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
			else if (i == OpenGlHelper.GL_FB_INCOMPLETE_DRAW_BUFFER)
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
			else if (i == OpenGlHelper.GL_FB_INCOMPLETE_READ_BUFFER)
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
			else
				throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + i);
		}
	}
	
	private void delete() {
		if (this.object_rb != 0)
			OpenGlHelper.glDeleteRenderbuffers(this.object_rb);
		
		TextureUtil.deleteTexture(this.object_cb);
		OpenGlHelper.glDeleteFramebuffers(this.object_fb);
		
		this.object_fb = 0;
	}
	
	UIUnit(int pw, int ph, boolean pd, boolean ps) {
		this.param_width = pw;
		this.param_height = ph;
		
		this.param_depth = pd;
		this.param_stencil = ps;
		
		this.create();
	}
	
	public void flush() {
		this.check();
		
		if (this.state_dirty) {
			this.delete();
			this.create();
			
			this.state_dirty = false;
		}
		
		int old_binding = GlStateManager.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
		OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.object_fb);
		
		GL11.glPushAttrib(GL11.GL_VIEWPORT_BIT | GL11.GL_TRANSFORM_BIT);
		
		GL11.glViewport(0, 0, this.param_width, this.param_height);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		
		this.flush0();
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		
		GL11.glPopAttrib();
		
		OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, old_binding);
	}
	
	abstract void flush0();
	
	public void render(@Nonnull Vector3f p0, @Nonnull Vector3f p1, @Nonnull Vector3f p2, @Nonnull Vector3f p3) {
		this.check();
		
		GL11.glPushAttrib(GL11.GL_TEXTURE_BIT);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.object_cb);
		
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buffer = tess.getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		buffer.pos(p0.x, p0.y, p0.z).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
		buffer.pos(p1.x, p1.y, p1.z).tex(1.0D, 0.0D).color(255, 255, 255, 255).endVertex();
		buffer.pos(p2.x, p2.y, p2.z).tex(1.0D, 1.0D).color(255, 255, 255, 255).endVertex();
		buffer.pos(p3.x, p3.y, p3.z).tex(0.0D, 1.0D).color(255, 255, 255, 255).endVertex();
		tess.draw();
		
		GL11.glPopAttrib();
	}
	
	public void release() {
		this.check();
		
		this.delete();
		this.release0();
	}
	
	void release0() {
	}
	
	public boolean available() {
		return this.object_fb != 0;
	}
	
	public void check() {
		Preconditions.checkState(this.available());
	}
	
	public void resize(int width, int height) {
		this.check();
		
		int oldW = this.param_width;
		int oldH = this.param_height;
		
		if (oldW != width || oldH != height) {
			this.param_width = width;
			this.param_height = height;
			
			this.state_dirty = true;
			
			this.resize0(oldW, oldH);
		}
	}
	
	void resize0(int oldWidth, int oldHeight) {
	}
	
	public int width() {
		this.check();
		
		return this.param_width;
	}
	
	public int height() {
		this.check();
		
		return this.param_height;
	}
}
