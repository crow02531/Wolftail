package net.wolftail.util.tracker;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;

@SideWith(section = GameSection.GAME_PLAYING)
public abstract class DiffVisitor {
	
	protected DiffVisitor dv;
	
	/**
	 * Mark a new begin of visiting a content diff. During this process
	 * only a single thread should be involved.
	 */
	public void jzBegin() {
		if(dv != null)
			dv.jzBegin();
	}
	
	/**
	 * Mark the end of visiting.
	 */
	public void jzEnd() {
		if(dv != null)
			dv.jzEnd();
	}
	
	/**
	 * Bind the target world to {@code dim}.
	 * 
	 * @param dim	the new target dimension, null indicates unbind
	 */
	public void jzBindWorld(DimensionType dim) {
		if(dv != null)
			dv.jzBindWorld(dim);
	}
	
	/**
	 * Bind the target chunk to {@code chunkPos} in target world.
	 * 
	 * @param chunkPos	the position of the chunk, null indicates unbind
	 */
	public void jzBindChunk(ChunkPos chunkPos) {
		if(dv != null)
			dv.jzBindChunk(chunkPos);
	}
	
	/**
	 * Identical to {@code jzBindChunk(new ChunkPos(chunkX, chunkZ))}.
	 */
	public void jzBindChunk(int chunkX, int chunkZ) {
		if(dv != null)
			dv.jzBindChunk(chunkX, chunkZ);
	}
	
	/**
	 * Bind the target block to {@code pos} in target world.
	 * 
	 * @param pos	the position of the block, null indicates unbind
	 */
	public void jzBindBlock(BlockPos pos) {
		if(dv != null)
			dv.jzBindBlock(pos);
	}
	
	/**
	 * Bind the target block to {@code index} in the target
	 * chunk of target world.
	 */
	public void jzBindBlock(short index) {
		if(dv != null)
			dv.jzBindBlock(index);
	}
	
	/**
	 * Set the daytime of target world.
	 */
	public void jzSetDaytime(long daytime) {
		if(dv != null)
			dv.jzSetDaytime(daytime);
	}
	
	/**
	 * Set the weather of target world.
	 */
	public void jzSetWeather(float rainStr, float thunderStr) {
		if(dv != null)
			dv.jzSetWeather(rainStr, thunderStr);
	}
	
	/**
	 * Set block state layer of the {@code index} chunk section in
	 * target chunk to {@code buf}.
	 * 
	 * @param index	the index of the section
	 * @param buf	the raw data of the block state layer, in the form
	 * 		of {@link net.minecraft.world.chunk.BlockStateContainer BlockStateContainer},
	 * 		null indicates empty
	 */
	public void jzSetSection(int index, ByteBuf buf) {
		if(dv != null)
			dv.jzSetSection(index, buf);
	}
	
	/**
	 * Set the block state of target block.
	 */
	public void jzSetState(@Nonnull IBlockState state) {
		if(dv != null)
			dv.jzSetState(state);
	}
	
	/**
	 * Set the tile entity of target block.
	 * 
	 * @param serialized	the serialized data of the tile entity, null
	 * 		indicates no tile entity
	 */
	public void jzSetTileEntity(NBTTagCompound serialized) {
		if(dv != null)
			dv.jzSetTileEntity(serialized);
	}
}
