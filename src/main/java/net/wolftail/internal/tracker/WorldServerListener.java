package net.wolftail.internal.tracker;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.wolftail.util.MoreBlockPos;

public final class WorldServerListener implements IWorldEventListener {
	
	@Override
	public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
		if (oldState != newState)
			((ExtTrackerChunk) worldIn.getChunkFromBlockCoords(pos)).wolftail_blockChanged(MoreBlockPos.toIndex(pos));
	}
	
	@Override
	public void notifyLightSet(BlockPos pos) {
		
	}
	
	@Override
	public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
		
	}
	
	@Override
	public void playSoundToAllNearExcept(EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x,
			double y, double z, float volume, float pitch) {
		
	}
	
	@Override
	public void playRecord(SoundEvent soundIn, BlockPos pos) {
		
	}
	
	@Override
	public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord,
			double xSpeed, double ySpeed, double zSpeed, int... parameters) {
		
	}
	
	@Override
	public void spawnParticle(int id, boolean ignoreRange, boolean p_190570_3_, double x, double y, double z,
			double xSpeed, double ySpeed, double zSpeed, int... parameters) {
		
	}
	
	@Override
	public void onEntityAdded(Entity entityIn) {
		
	}
	
	@Override
	public void onEntityRemoved(Entity entityIn) {
		
	}
	
	@Override
	public void broadcastSound(int soundID, BlockPos pos, int data) {
		
	}
	
	@Override
	public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {
		
	}
	
	@Override
	public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
		
	}
}
