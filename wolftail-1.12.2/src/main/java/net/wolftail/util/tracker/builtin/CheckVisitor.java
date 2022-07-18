package net.wolftail.util.tracker.builtin;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.wolftail.util.tracker.DiffVisitor;

public class CheckVisitor extends DiffVisitor {
	
	private volatile Thread processor;
	
	private boolean bindWorld;
	private boolean bindChunk;
	private boolean bindBlock;
	
	private void checkThread() {
		if(Thread.currentThread() != this.processor)
			throw new IllegalStateException("Multi threads involved");
	}
	
	public CheckVisitor(DiffVisitor dv) {
		this.dv = dv;
	}
	
	@Override
	public void jzBegin() {
		
	}
	
	@Override
	public void jzEnd() {
		
	}
	
	@Override
	public void jzBindWorld(DimensionType dim) {
		
	}
	
	@Override
	public void jzBindChunk(ChunkPos chunkPos) {
		
	}
	
	@Override
	public void jzBindChunk(int chunkX, int chunkZ) {
		
	}
	
	@Override
	public void jzBindBlock(BlockPos pos) {
		
	}
	
	@Override
	public void jzBindBlock(short index) {
		
	}
	
	@Override
	public void jzSetDaytime(long daytime) {
		
	}
	
	@Override
	public void jzSetWeather(float rainStr, float thunderStr) {
		
	}
	
	@Override
	public void jzSetSection(int index, ByteBuf buf) {
		
	}
	
	@Override
	public void jzSetState(@Nonnull IBlockState state) {
		
	}
	
	@Override
	public void jzSetTileEntity(NBTTagCompound serialized) {
		
	}
}
