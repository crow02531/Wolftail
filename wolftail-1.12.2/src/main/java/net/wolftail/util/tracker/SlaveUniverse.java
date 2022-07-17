package net.wolftail.util.tracker;

import java.util.UUID;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
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
	 * @param dim	the new target dimension, null indicates unbind
	 */
	protected abstract void jzBindWorld(DimensionType dim);
	
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
	 * Change the target chunk. Must bind to a target world before.
	 * 
	 * @param chunkPos	the position of the chunk, null indicates unbind
	 */
	protected abstract void jzBindChunk(ChunkPos chunkPos);
	
	/**
	 * Identical to {@code jzBindChunk(new ChunkPos(chunkX, chunkZ))}.
	 * 
	 * @param chunkX	the x coordinate of the new target
	 * @param chunkZ	the z coordinate of the new target
	 * 
	 * @see #jzBindChunk(ChunkPos)
	 */
	protected abstract void jzBindChunk(int chunkX, int chunkZ);
	
	/**
	 * Set the block state in the target chunk.
	 */
	protected abstract void jzSetBlock(int localX, int localY, int localZ, @Nonnull IBlockState state);
	
	/**
	 * Set the block state layer of a chunk section in the target chunk.
	 * 
	 * @param index	the index of the section, between 0 and 15
	 * @param buf	the raw data of the block state layer, in the form of
	 * 		{@link net.minecraft.world.chunk.BlockStateContainer BlockStateContainer},
	 * 		null indicates empty
	 */
	protected abstract void jzSetSection(int index, ByteBuf buf);
	
	/**
	 * Set the tile entity in the target chunk.
	 * 
	 * @param serialized	the serialized data of the tile entity, null indicates no tile entity
	 */
	protected abstract void jzSetTileEntity(int localX, int localY, int localZ, NBTTagCompound serialized);
	
	/**
	 * Generate a new entity in target world.
	 * 
	 * @param entityID	the temporary id of the entity
	 * @param uniqueID	the persistent id of the entity
	 * @param type		the type of the entity
	 */
	protected abstract void jzGenEntity(int entityID, UUID uniqueID, Class<? extends Entity> type);
	
	/**
	 * Delete an existent entity in target world.
	 * 
	 * @param entityID	the temporary id of the entity
	 */
	protected abstract void jzDeleteEntity(int entityID);
	
	/**
	 * Change the target entity. Must bind to a target world before and the
	 * entity already exists.
	 * 
	 * @param entityID	the temporary id of the entity, negative indicates unbind
	 */
	protected abstract void jzBindEntity(int entityID);
	
	/**
	 * Set the shape of the target entity.
	 */
	protected abstract void jzSetEntityShape(float width, float height);
	
	/**
	 * Set the position state of the target entity.
	 */
	protected abstract void jzSetEntityPosition(double x, double y, double z);
	
	/**
	 * Set the rotation state of the target entity.
	 */
	protected abstract void jzSetEntityRotation(float yaw, float pitch);
}
