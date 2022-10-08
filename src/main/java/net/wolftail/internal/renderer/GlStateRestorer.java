package net.wolftail.internal.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;

public final class GlStateRestorer {

    private GlStateRestorer() {
    }

    private static boolean alpha_test;
    private static int alpha_func;
    private static float alpha_ref;

    private static boolean blend;
    private static int blend_dst;
    private static int blend_dst_alpha;
    private static int blend_src;
    private static int blend_src_alpha;

    private static float clear_r;
    private static float clear_g;
    private static float clear_b;
    private static float clear_a;
    private static double clear_d;

    private static boolean color_logic_op;
    private static int color_logic_op_opcode;

    private static boolean color_mask_r;
    private static boolean color_mask_g;
    private static boolean color_mask_b;
    private static boolean color_mask_a;

    private static boolean color_material;
    private static int color_material_face;
    private static int color_material_mode;

    private static float color_r;
    private static float color_g;
    private static float color_b;
    private static float color_a;

    private static boolean cull_face;
    private static int cull_face_mode;

    private static boolean depth_test;
    private static int depth_func;
    private static boolean depth_mask;

    private static boolean fog;
    private static int fog_mode;
    private static float fog_density;
    private static float fog_start;
    private static float fog_end;

    private static final boolean[] light = new boolean[8];

    private static boolean lighting;

    private static boolean normalize;

    private static boolean polygon_offset_line;
    private static boolean polygon_offset_fill;
    private static float polygon_offset_factor;
    private static float polygon_offset_units;

    private static boolean rescale_normal;

    private static boolean tex_gen_s;
    private static int tex_gen_s_coord;
    private static int tex_gen_s_param;
    private static boolean tex_gen_t;
    private static int tex_gen_t_coord;
    private static int tex_gen_t_param;
    private static boolean tex_gen_r;
    private static int tex_gen_r_coord;
    private static int tex_gen_r_param;
    private static boolean tex_gen_q;
    private static int tex_gen_q_coord;
    private static int tex_gen_q_param;

    private static final boolean[] texture_2d = new boolean[8];
    private static final int[] texture_name = new int[8];
    private static int active_texture_unit;

    private static int shade_mode;

    // call from logic client & no nesting
    public static void store() {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        int old_mode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();

        GL11.glMatrixMode(old_mode);

        alpha_test = GlStateManager.alphaState.alphaTest.currentState;
        alpha_func = GlStateManager.alphaState.func;
        alpha_ref = GlStateManager.alphaState.ref;

        blend = GlStateManager.blendState.blend.currentState;
        blend_dst = GlStateManager.blendState.dstFactor;
        blend_dst_alpha = GlStateManager.blendState.dstFactorAlpha;
        blend_src = GlStateManager.blendState.srcFactor;
        blend_src_alpha = GlStateManager.blendState.srcFactorAlpha;

        clear_r = GlStateManager.clearState.color.red;
        clear_g = GlStateManager.clearState.color.green;
        clear_b = GlStateManager.clearState.color.blue;
        clear_a = GlStateManager.clearState.color.alpha;
        clear_d = GlStateManager.clearState.depth;

        color_logic_op = GlStateManager.colorLogicState.colorLogicOp.currentState;
        color_logic_op_opcode = GlStateManager.colorLogicState.opcode;

        color_mask_r = GlStateManager.colorMaskState.red;
        color_mask_g = GlStateManager.colorMaskState.green;
        color_mask_b = GlStateManager.colorMaskState.blue;
        color_mask_a = GlStateManager.colorMaskState.alpha;

        color_material = GlStateManager.colorMaterialState.colorMaterial.currentState;
        color_material_face = GlStateManager.colorMaterialState.face;
        color_material_mode = GlStateManager.colorMaterialState.mode;

        color_r = GlStateManager.colorState.red;
        color_g = GlStateManager.colorState.green;
        color_b = GlStateManager.colorState.blue;
        color_a = GlStateManager.colorState.alpha;

        cull_face = GlStateManager.cullState.cullFace.currentState;
        cull_face_mode = GlStateManager.cullState.mode;

        depth_test = GlStateManager.depthState.depthTest.currentState;
        depth_func = GlStateManager.depthState.depthFunc;
        depth_mask = GlStateManager.depthState.maskEnabled;

        fog = GlStateManager.fogState.fog.currentState;
        fog_mode = GlStateManager.fogState.mode;
        fog_density = GlStateManager.fogState.density;
        fog_start = GlStateManager.fogState.start;
        fog_end = GlStateManager.fogState.end;

        for (int i = 0; i < 8; ++i)
            light[i] = GlStateManager.lightState[i].currentState;

        lighting = GlStateManager.lightingState.currentState;

        normalize = GlStateManager.normalizeState.currentState;

        polygon_offset_line = GlStateManager.polygonOffsetState.polygonOffsetLine.currentState;
        polygon_offset_fill = GlStateManager.polygonOffsetState.polygonOffsetFill.currentState;
        polygon_offset_factor = GlStateManager.polygonOffsetState.factor;
        polygon_offset_units = GlStateManager.polygonOffsetState.units;

        rescale_normal = GlStateManager.rescaleNormalState.currentState;

        tex_gen_s = GlStateManager.texGenState.s.textureGen.currentState;
        tex_gen_s_coord = GlStateManager.texGenState.s.coord;
        tex_gen_s_param = GlStateManager.texGenState.s.param;
        tex_gen_t = GlStateManager.texGenState.t.textureGen.currentState;
        tex_gen_t_coord = GlStateManager.texGenState.t.coord;
        tex_gen_t_param = GlStateManager.texGenState.t.param;
        tex_gen_r = GlStateManager.texGenState.r.textureGen.currentState;
        tex_gen_r_coord = GlStateManager.texGenState.r.coord;
        tex_gen_r_param = GlStateManager.texGenState.r.param;
        tex_gen_q = GlStateManager.texGenState.q.textureGen.currentState;
        tex_gen_q_coord = GlStateManager.texGenState.q.coord;
        tex_gen_q_param = GlStateManager.texGenState.q.param;

        for (int i = 0; i < 8; ++i) {
            texture_2d[i] = GlStateManager.textureState[i].texture2DState.currentState;
            texture_name[i] = GlStateManager.textureState[i].textureName;
        }

        active_texture_unit = GlStateManager.activeTextureUnit;
        shade_mode = GlStateManager.activeShadeModel;
    }

    // call from logic client & no nesting
    public static void restore() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();

        GL11.glPopAttrib();

        GlStateManager.alphaState.alphaTest.currentState = alpha_test;
        GlStateManager.alphaState.func = alpha_func;
        GlStateManager.alphaState.ref = alpha_ref;

        GlStateManager.blendState.blend.currentState = blend;
        GlStateManager.blendState.dstFactor = blend_dst;
        GlStateManager.blendState.dstFactorAlpha = blend_dst_alpha;
        GlStateManager.blendState.srcFactor = blend_src;
        GlStateManager.blendState.srcFactorAlpha = blend_src_alpha;

        GlStateManager.clearState.color.red = clear_r;
        GlStateManager.clearState.color.green = clear_g;
        GlStateManager.clearState.color.blue = clear_b;
        GlStateManager.clearState.color.alpha = clear_a;
        GlStateManager.clearState.depth = clear_d;

        GlStateManager.colorLogicState.colorLogicOp.currentState = color_logic_op;
        GlStateManager.colorLogicState.opcode = color_logic_op_opcode;

        GlStateManager.colorMaskState.red = color_mask_r;
        GlStateManager.colorMaskState.green = color_mask_g;
        GlStateManager.colorMaskState.blue = color_mask_b;
        GlStateManager.colorMaskState.alpha = color_mask_a;

        GlStateManager.colorMaterialState.colorMaterial.currentState = color_material;
        GlStateManager.colorMaterialState.face = color_material_face;
        GlStateManager.colorMaterialState.mode = color_material_mode;

        GlStateManager.colorState.red = color_r;
        GlStateManager.colorState.green = color_g;
        GlStateManager.colorState.blue = color_b;
        GlStateManager.colorState.alpha = color_a;

        GlStateManager.cullState.cullFace.currentState = cull_face;
        GlStateManager.cullState.mode = cull_face_mode;

        GlStateManager.depthState.depthTest.currentState = depth_test;
        GlStateManager.depthState.depthFunc = depth_func;
        GlStateManager.depthState.maskEnabled = depth_mask;

        GlStateManager.fogState.fog.currentState = fog;
        GlStateManager.fogState.mode = fog_mode;
        GlStateManager.fogState.density = fog_density;
        GlStateManager.fogState.start = fog_start;
        GlStateManager.fogState.end = fog_end;

        for (int i = 0; i < 8; ++i)
            GlStateManager.lightState[i].currentState = light[i];

        GlStateManager.lightingState.currentState = lighting;

        GlStateManager.normalizeState.currentState = normalize;

        GlStateManager.polygonOffsetState.polygonOffsetLine.currentState = polygon_offset_line;
        GlStateManager.polygonOffsetState.polygonOffsetFill.currentState = polygon_offset_fill;
        GlStateManager.polygonOffsetState.factor = polygon_offset_factor;
        GlStateManager.polygonOffsetState.units = polygon_offset_units;

        GlStateManager.rescaleNormalState.currentState = rescale_normal;

        GlStateManager.texGenState.s.textureGen.currentState = tex_gen_s;
        GlStateManager.texGenState.s.coord = tex_gen_s_coord;
        GlStateManager.texGenState.s.param = tex_gen_s_param;
        GlStateManager.texGenState.t.textureGen.currentState = tex_gen_t;
        GlStateManager.texGenState.t.coord = tex_gen_t_coord;
        GlStateManager.texGenState.t.param = tex_gen_t_param;
        GlStateManager.texGenState.r.textureGen.currentState = tex_gen_r;
        GlStateManager.texGenState.r.coord = tex_gen_r_coord;
        GlStateManager.texGenState.r.param = tex_gen_r_param;
        GlStateManager.texGenState.q.textureGen.currentState = tex_gen_q;
        GlStateManager.texGenState.q.coord = tex_gen_q_coord;
        GlStateManager.texGenState.q.param = tex_gen_q_param;

        for (int i = 0; i < 8; ++i) {
            GlStateManager.textureState[i].texture2DState.currentState = texture_2d[i];
            GlStateManager.textureState[i].textureName = texture_name[i];
        }

        GlStateManager.activeTextureUnit = active_texture_unit;
        GlStateManager.activeShadeModel = shade_mode;
    }
}
