package net.wolftail.util.client.renderer;

import java.nio.IntBuffer;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

import com.google.common.base.Preconditions;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.Sealed;
import net.wolftail.api.lifecycle.SideWith;

@Sealed
@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_CLIENT)
public abstract class UIUnit {

	int param_width; // read-only
	int param_height; // read-only

	private int object_tex; // may changed during the unit's lifecycle, fb's color buffer
	private int object_fb; // remains unchanged during the unit's lifecycle

	private void create_tex() {
		GL11.glPushAttrib(GL11.GL_TEXTURE_BIT);

		this.object_tex = GL11.glGenTextures();

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.object_tex);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, this.param_width, this.param_height, 0, GL11.GL_RGBA,
				GL11.GL_UNSIGNED_BYTE, (IntBuffer) null);

		GL11.glPopAttrib();
	}

	private void delete_tex() {
		GL11.glDeleteTextures(this.object_tex);

		this.object_tex = 0;
	}

	private void bind_tex2fb() {
		this.bindAndExecute(() -> {
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, object_tex,
					0);
		});
	}

	void bindAndExecute(Runnable r) {
		int old_binding = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.object_fb);
		r.run();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, old_binding);
	}

	UIUnit(int pw, int ph) {
		this.param_width = pw;
		this.param_height = ph;

		this.create_tex();

		this.object_fb = GL30.glGenFramebuffers();
		this.bind_tex2fb();
	}

	public final void flush() {
		this.check();

		this.flush0();
	}

	abstract void flush0();

	public final void render(@Nonnull Vector3f p0, @Nonnull Vector3f p1, @Nonnull Vector3f p2, @Nonnull Vector3f p3) {
		this.check();

		int old_binding = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.object_tex);

		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buffer = tess.getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(p0.x, p0.y, p0.z).tex(0.0D, 0.0D).endVertex();
		buffer.pos(p1.x, p1.y, p1.z).tex(1.0D, 0.0D).endVertex();
		buffer.pos(p2.x, p2.y, p2.z).tex(1.0D, 1.0D).endVertex();
		buffer.pos(p3.x, p3.y, p3.z).tex(0.0D, 1.0D).endVertex();
		tess.draw();

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, old_binding);
	}

	public final void release() {
		this.check();

		this.delete_tex();

		GL30.glDeleteFramebuffers(this.object_fb);
		this.object_fb = 0;

		this.release0();
	}

	void release0() {
	}

	public final boolean available() {
		return this.object_tex != 0;
	}

	public final void check() {
		Preconditions.checkState(this.available());
	}

	public final void resize(int width, int height) {
		this.check();

		int oldW = this.param_width;
		int oldH = this.param_height;

		if (oldW != width || oldH != height) {
			this.param_width = width;
			this.param_height = height;

			this.delete_tex();
			this.create_tex();
			this.bind_tex2fb();

			this.resize0(oldW, oldH);
		}
	}

	void resize0(int oldWidth, int oldHeight) {
	}

	public final int width() {
		this.check();

		return this.param_width;
	}

	public final int height() {
		this.check();

		return this.param_height;
	}
}
