package net.wolftail.util.client.renderer;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.LogicType;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.util.MoreByteBufs;

@SideWith(section = GameSection.GAME_PLAYING, thread = LogicType.LOGIC_CLIENT)
public final class VanillaUnit extends UIUnit {

    public VanillaUnit(int pw, int ph) {
        super(pw, ph);
    }

    /**
	 * Set all {@code prev*} to its current value.
	 */
    public void pMarch() {

    }

    public void pPartialTicks(float partialTicks) {

    }

    public void pCamera(double x, double y, double z, float yaw, float pitch, float roll, float fovy) {

    }

    public void pTime(int time) {

    }

    public void pWeather(float rainStr, float thunderStr) {

    }

    public void pSetState(@Nonnull BlockPos pos, IBlockState state) {

    }

    public void pSetSection(int chunkX, int chunkY, int index, ByteBuf buf) {

    }

    public void pSetSection(int chunkX, int chunkY, int index, ExtendedBlockStorage src) {

    }

    public void pSetTileEntity(@Nonnull BlockPos pos, ByteBuf buf) {
        this.pSetTileEntity0(pos, buf == null ? null : MoreByteBufs.readTag(buf));
    }

    public void pSetTileEntity(@Nonnull BlockPos pos, NBTTagCompound src) {
        this.pSetTileEntity0(pos, src == null ? null : src.copy());
    }

    private void pSetTileEntity0(BlockPos pos, NBTTagCompound nbt) {

    }

    @Override
    void flush0() {

    }
}
