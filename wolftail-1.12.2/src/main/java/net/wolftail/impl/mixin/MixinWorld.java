package net.wolftail.impl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.wolftail.impl.SharedImpls;
import net.wolftail.impl.SharedImpls.H2;

//ContentTracker Supporter
@Mixin(World.class)
public abstract class MixinWorld {
	
	@Shadow
	public abstract Chunk getChunkFromBlockCoords(BlockPos pos);
	
	@Inject(method = "markChunkDirty", at = @At(value = "INVOKE", target = "markDirty()V"))
	private void onMarkChunkDirty(BlockPos pos, TileEntity unusedTileEntity, CallbackInfo info) {
		SharedImpls.as(this.getChunkFromBlockCoords(pos)).wolftail_tileEntityChanged(H2.toIndex(pos));
	}
	
	@Inject(method = "addTileEntity", at = @At("RETURN"))
	private void onAddTileEntity(TileEntity tile, CallbackInfoReturnable<Boolean> info) {
		if(info.getReturnValueZ())
			SharedImpls.as(this.getChunkFromBlockCoords(tile.getPos())).wolftail_tileEntityChanged(H2.toIndex(tile.getPos()));
	}
	
	@Inject(method = "removeTileEntity", at = @At("RETURN"))
	private void onRemoveTileEntity(BlockPos pos, CallbackInfo info) {
		SharedImpls.as(this.getChunkFromBlockCoords(pos)).wolftail_tileEntityChanged(H2.toIndex(pos));
	}
}
