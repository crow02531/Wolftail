package net.wolftail.util.tracker;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.DimensionType;
import net.minecraft.world.chunk.BlockStateContainer;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;

@SideWith(section = GameSection.GAME_PLAYING)
public interface DiffVisitor {
	
	/**
	 * The lock object for this diff visitor. It should always return the
	 * same value.
	 * 
	 * @return the lock object
	 */
	@Nonnull Object lockObject();
	
	/**
	 * Mark a new begin of visiting a content diff. During this process
	 * only a single thread should be involved, and this thread must hold
	 * the lock object of this diff visitor.
	 * 
	 * @see #lockObject()
	 */
	void jzBegin();
	
	/**
	 * Mark the end of visiting.
	 */
	void jzEnd();
	
	/**
	 * Bind the target world to {@code dim}. No matter what
	 * {@code dim} is, it will unbind target chunk and block.
	 * 
	 * @param dim	the new target dimension
	 */
	void jzBindWorld(@Nonnull DimensionType dim);
	
	/**
	 * Bind the target chunk to {@code (chunkX, chunkZ)} in
	 * target world. It will unbind target block. Before bind
	 * to a chunk a world must be bound first.
	 * 
	 * @param chunkX	-1875000 <= chunkX < 1875000
	 * @param chunkZ	-1875000 <= chunkZ < 1875000
	 */
	void jzBindChunk(int chunkX, int chunkZ);
	
	/**
	 * Bind the target block to {@code index} in the target
	 * chunk of target world. Before bind to a block a chunk
	 * must be bound first.
	 * 
	 * @param index	the index of the block, have no restrict
	 * 		of course
	 */
	void jzBindBlock(short index);
	
	/**
	 * Unbind the target world. It will unbind target chunk
	 * and target block automatically. Have no effect if target
	 * world hasn't bounden to something.
	 */
	void jzUnbindWorld();
	
	/**
	 * Unbind the target chunk. It will unbind target block
	 * automatically. Have no effect if target chunk hasn't
	 * bounden to something.
	 */
	void jzUnbindChunk();
	
	/**
	 * Unbind the target block. Have no effect if target block
	 * hasn't bounden to something.
	 */
	void jzUnbindBlock();
	
	/**
	 * Set the daytime of target world. Must have bounden to a
	 * world before.
	 * 
	 * @param daytime	the daytime
	 */
	void jzSetDaytime(long daytime);
	
	/**
	 * Set the weather of target world. Must have bounden to a
	 * world before.
	 * 
	 * @param rainStr		the raining strength
	 * @param thunderStr	the thundering strength
	 */
	void jzSetWeather(float rainStr, float thunderStr);
	
	/**
	 * Set block state layer of the {@code index} chunk section in
	 * target chunk to {@code buf}. Must have bounden to a chunk
	 * before.
	 * 
	 * @param index				the index of the section, between 0 and 15
	 * @param blockStateLayer	the block state layer of the section, null
	 * 		indicates empty, caller is responsible of making a copy
	 */
	void jzSetSection(int index, BlockStateContainer blockStateLayer);
	
	/**
	 * Set the block state of target block. Must have bounden to a block
	 * before.
	 * 
	 * @param state	the block state
	 */
	void jzSetState(@Nonnull IBlockState state);
	
	/**
	 * Set the tile entity of target block. Must have bounden to a block
	 * before.
	 * 
	 * @param serialized	the serialized data of the tile entity, null
	 * 		indicates no tile entity, caller is responsible of making a copy
	 */
	void jzSetTileEntity(NBTTagCompound serialized);
}
