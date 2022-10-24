package net.wolftail.util.tracker;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.DimensionType;

// TODO design a method for tracking entities
public interface DiffVisitor {
	
	/**
	 * Mark a new begin of visiting a content diff. During this process only a
	 * single thread should be involved.
	 */
	void jzBegin();
	
	/**
	 * Mark the end of visiting.
	 */
	void jzEnd();
	
	/**
	 * Bind the target world to {@code dim}. No matter what {@code dim} is, it will
	 * unbind target chunk and block.
	 * 
	 * @param dim the new target dimension
	 */
	void jzBindWorld(@Nonnull DimensionType dim);
	
	/**
	 * Bind the target chunk to {@code (chunkX, chunkZ)} in target world. It will
	 * unbind target block. Before bind to a chunk a world must be bound first.
	 * 
	 * @param chunkX {@code -1875000 <= chunkX < 1875000}
	 * @param chunkZ {@code -1875000 <= chunkZ < 1875000}
	 */
	void jzBindChunk(int chunkX, int chunkZ);
	
	/**
	 * Bind the target block to {@code index} in the target chunk of target world.
	 * Before bind to a block a chunk must be bound first.
	 * 
	 * @param index the index of the block, have no restrict of course
	 */
	void jzBindBlock(short index);
	
	/**
	 * Unbind the target world. It will unbind target chunk and target block
	 * automatically. Have no effect if target world hasn't bounden to something.
	 */
	void jzUnbindWorld();
	
	/**
	 * Unbind the target chunk. It will unbind target block automatically. Have no
	 * effect if target chunk hasn't bounden to something.
	 */
	void jzUnbindChunk();
	
	/**
	 * Unbind the target block. Have no effect if target block hasn't bounden to
	 * something.
	 */
	void jzUnbindBlock();
	
	/**
	 * Set the daytime of target world. Must have bounden to a world before.
	 * 
	 * @param daytime the daytime, between 0 and 24000
	 */
	void jzSetDaytime(int daytime);
	
	/**
	 * Set the weather of target world. Must have bounden to a world before.
	 * 
	 * @param rainStr    the raining strength
	 * @param thunderStr the thundering strength
	 */
	void jzSetWeather(float rainStr, float thunderStr);
	
	/**
	 * Set block state layer of the {@code index} chunk section in target chunk to
	 * {@code buf}. Must have bounden to a chunk before.
	 * 
	 * <p>
	 * This method should only perform read operations over {@code buf}'s readable
	 * bytes. But it's not necessary to read till unreadable. Callers are
	 * responsible of making all {@code buf}'s readable bytes unchanged before the
	 * method returns.
	 * </p>
	 * 
	 * @param index the index of the section, between 0 and 15
	 * @param buf   a buf whose all readable bytes composing the raw data of one
	 *              block state layer, null indicates empty section.
	 */
	void jzSetSection(int index, ByteBuf buf);
	
	/**
	 * Set the block state of target block. Must have bounden to a block before.
	 * 
	 * @param state the block state
	 */
	void jzSetState(@Nonnull IBlockState state);
	
	/**
	 * Set the tile entity of target block. Must have bounden to a block before.
	 * 
	 * <p>
	 * This method should only perform read operations over {@code buf}'s readable
	 * bytes. But it's not necessary to read till unreadable. Callers are
	 * responsible of making all {@code buf}'s readable bytes unchanged before the
	 * method returns.
	 * </p>
	 * 
	 * @param buf a buf whose all readable bytes composing the raw data of one
	 *            compound tag, null indicates no tile entity in target block
	 */
	void jzSetTileEntity(ByteBuf buf);
}
