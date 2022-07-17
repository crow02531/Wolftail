package net.wolftail.util.tracker;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.DimensionType;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;

@SideWith(section = GameSection.GAME_PLAYING)
public abstract class SlaveUniverse {
	
	/**
	 * Mark a new begin of receiving the analyzed data of a content diff.
	 */
	protected abstract void jzBegin();
	
	/**
	 * Mark the end of receiving.
	 */
	protected abstract void jzEnd();
	
	protected abstract void jzWorld(DimensionType dim);
	protected abstract void jzChunk(int chunkX, int chunkY);
	
	protected abstract void jzSetDaytime(long daytime);
	protected abstract void jzSetRainingStr(float str);
	protected abstract void jzSetThunderingStr(float str);
	
	protected abstract void jzSetBlock(int localX, int localY, int localZ, @Nonnull IBlockState state);
	protected abstract void jzSetSection(int index, ByteBuf buf);
	
	protected abstract void jzSetTileEntity(int localX, int localY, int localZ, NBTTagCompound serialized);
}
