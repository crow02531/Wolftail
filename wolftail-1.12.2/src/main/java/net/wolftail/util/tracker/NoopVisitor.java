package net.wolftail.util.tracker;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.DimensionType;
import net.minecraft.world.chunk.BlockStateContainer;

final class NoopVisitor implements DiffVisitor {
	
	@Override
	public Object lockObject() {
		return this;
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
	public void jzBindChunk(int chunkX, int chunkZ) {
		
	}
	
	@Override
	public void jzBindBlock(short index) {
		
	}
	
	@Override
	public void jzUnbindWorld() {
		
	}
	
	@Override
	public void jzUnbindChunk() {
		
	}
	
	@Override
	public void jzUnbindBlock() {
		
	}
	
	@Override
	public void jzSetDaytime(long daytime) {
		
	}
	
	@Override
	public void jzSetWeather(float rainStr, float thunderStr) {
		
	}
	
	@Override
	public void jzSetSection(int index, BlockStateContainer blockStateLayer) {
		
	}
	
	@Override
	public void jzSetState(IBlockState state) {
		
	}
	
	@Override
	public void jzSetTileEntity(NBTTagCompound serialized) {
		
	}
}
