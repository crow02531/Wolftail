package net.wolftail.impl.tracker.mixin;

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
import net.wolftail.impl.tracker.ExtTrackerChunk;
import net.wolftail.impl.util.MoreBlockPos;

//handle tile entity changes
@Mixin(World.class)
public abstract class MixinWorld {
	
	@Shadow
	public abstract Chunk getChunkFromBlockCoords(BlockPos pos);
	
	@Inject(method = "markChunkDirty", at = @At(value = "INVOKE", target = "markDirty()V"))
	private void on_markChunkDirty_invoke_markDirty(BlockPos pos, TileEntity unusedTileEntity, CallbackInfo ci) {
		this.on_removeTileEntity_return(pos, null);
	}
	
	@Inject(method = "addTileEntity", at = @At("RETURN"))
	private void on_addTileEntity_return(TileEntity tile, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValueZ())
			this.on_removeTileEntity_return(tile.getPos(), null);
	}
	
	@Inject(method = "removeTileEntity", at = @At("RETURN"))
	private void on_removeTileEntity_return(BlockPos pos, CallbackInfo ci) {
		((ExtTrackerChunk) this.getChunkFromBlockCoords(pos)).wolftail_tileEntityChanged(MoreBlockPos.toIndex(pos));
	}
}
