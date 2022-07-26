package net.wolftail.util.tracker;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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
	 * {@code dim} is, it will unbind target chunk, section
	 * and block.
	 * 
	 * @param dim	the new target dimension
	 */
	void jzBindWorld(@Nonnull ResourceKey<Level> dim);
	
	/**
	 * Bind the target chunk to {@code (chunkX, chunkZ)} in
	 * target world. It will unbind target section and block.
	 * Before bind to a chunk a world must be bound first.
	 * 
	 * @param chunkX	{@code -1875000 <= chunkX < 1875000}
	 * @param chunkZ	{@code -1875000 <= chunkZ < 1875000}
	 */
	void jzBindChunk(int chunkX, int chunkZ);
	
	/**
	 * Bind the target section to {@code index} in target chunk.
	 * It will unbind target block. Before bind to a section a
	 * chunk must be bound first.
	 * 
	 * @param index	the section index, {@code -524288 <= index <= 127}
	 */
	void jzBindSection(int index);
	
	/**
	 * Bind the target block to {@code index} in the target
	 * section. Before bind to a block a section must be bound
	 * first.
	 * 
	 * @param index	the block index, the 4 high-order bits should
	 * 		be zero
	 */
	void jzBindBlock(short index);
	
	/**
	 * Unbind the target world. It will unbind target chunk,
	 * section and block automatically. Have no effect if target
	 * world binds to nothing.
	 */
	void jzUnbindWorld();
	
	/**
	 * Unbind the target chunk. It will unbind target section
	 * and block automatically. Have no effect if target chunk
	 * binds to nothing.
	 */
	void jzUnbindChunk();
	
	/**
	 * Unbind the target section. It will unbind target block
	 * automatically. Have no effect if target section binds to
	 * nothing.
	 */
	void jzUnbindSection();
	
	/**
	 * Unbind the target block. Have no effect if target block
	 * binds to nothing.
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
	 * @param rainLv	the rain level
	 * @param thunderLv	the thunder level
	 */
	void jzSetWeather(float rainLv, float thunderLv);
	
	/**
	 * Set block state layer of the target section in to {@code buf}.
	 * Must have bounden to a section before.
	 * 
	 * <p>
	 * This method should only perform read operations over {@code buf}'s readable
	 * bytes.
	 * </p>
	 * 
	 * @param buf	a buf whose all readable bytes composing the raw data of one block
	 * 		state layer, null indicates empty section.
	 */
	void jzSetSection(ByteBuf buf);
	
	/**
	 * Set the block state of target block. Must have bounden to a block
	 * before.
	 * 
	 * @param state	the block state
	 */
	void jzSetState(@Nonnull BlockState state);
	
	/**
	 * Set the tile entity of target block. Must have bounden to a block
	 * before.
	 * 
	 * <p>
	 * This method should only perform read operations over {@code buf}'s readable
	 * bytes.
	 * </p>
	 * 
	 * @param buf	a buf whose all readable bytes composing the raw data of one compound
	 * 		tag, null indicates no tile entity in target block
	 */
	void jzSetTileEntity(ByteBuf buf);
}
