package net.wolftail.util.client.renderer;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;

@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_CLIENT)
public final class VanillaUpdater {

    private WorldClient world;
    private EntityPlayerSP player;
    
    VanillaUpdater(WorldClient w, EntityPlayerSP p) {
        this.world = w;
        this.player = p;
    }

    /**
	 * Reset to the initial state.
	 */
    public void clear() {
        
    }

    public void setCamera(double x, double y, double z, float yaw, float pitch, float roll, float fovy) {
        EntityPlayerSP p = this.player;

        p.setPosition(x, y, z);
        p.rotationYaw = yaw;
        p.rotationPitch = pitch;
        p.cameraYaw = roll;
        p.cameraPitch = fovy;
    }

    public void setTime(int time) {
        this.world.setWorldTime(time);
    }

    public void setWeather(float rainStr, float thunderStr) {
        this.world.rainingStrength = rainStr;
        this.world.thunderingStrength = thunderStr;
    }

    public void setBlockState(@Nonnull BlockPos pos, IBlockState state) {
        int cx = pos.getX() >> 4;
        int cz = pos.getZ() >> 4;
        state = state == null ? Blocks.AIR.getDefaultState() : state;

        ChunkProviderClient cp = this.world.getChunkProvider();

        for (int x = -1; x <= 1; ++x) {
            for (int z = -1; z <= 1; ++z) {
                if (!cp.isChunkGeneratedAt(cx + x, cz + z))
                    cp.loadChunk(cx + x, cz + z);
            }
        }

        this.world.setBlockState(pos, state);
    }
}
