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
	
	final Object LOCK_OBJECT = new Object();
	
	/**
	 * Mark a new begin of a receiving process. During this process
	 * only one thread will invoke jz* methods.
	 */
	protected abstract void jzBegin();
	
	/**
	 * Mark the end of receiving.
	 */
	protected abstract void jzEnd();
	
	/**
	 * Change the target world.
	 * 
	 * @param dim	the new target dimension
	 */
	protected abstract void jzWorld(DimensionType dim);
	
	/**
	 * Change the target chunk.
	 * 
	 * @param chunkX	the x coordinate of the new target
	 * @param chunkZ	the z coordinate of the new target
	 */
	protected abstract void jzChunk(int chunkX, int chunkZ);
	
	/**
	 * Set the daytime of current target world.
	 * 
	 * @param daytime	the daytime
	 */
	protected abstract void jzSetDaytime(long daytime);
	
	/**
	 * Set the raining strength of current target world.
	 * 
	 * @param str	the raining strength
	 */
	protected abstract void jzSetRainingStr(float str);
	
	/**
	 * Set the thundering strength of current target world.
	 * 
	 * @param str	the thundering strength
	 */
	protected abstract void jzSetThunderingStr(float str);
	
	/**
	 * Set a block state in the current target chunk of current target world.
	 */
	protected abstract void jzSetBlock(int localX, int localY, int localZ, @Nonnull IBlockState state);
	
	/**
	 * Set the block state layer of a chunk section in the current target chunk of current target world.
	 * 
	 * @param index	the index of the section, between 0 and 15
	 * @param buf	the raw data of the block state layer, in the form of
	 * 		{@link net.minecraft.world.chunk.BlockStateContainer BlockStateContainer},
	 * 		null indicates empty
	 */
	protected abstract void jzSetSection(int index, ByteBuf buf);
	
	/**
	 * Set a tile entity in the current target chunk of current target world.
	 * 
	 * @param serialized	the serialized data of the tile entity, null indicates no tile entity
	 */
	protected abstract void jzSetTileEntity(int localX, int localY, int localZ, NBTTagCompound serialized);
}
