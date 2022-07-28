package net.wolftail.impl.tracker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.world.chunk.Chunk;
import net.wolftail.impl.tracker.ExtTrackerChunk;
import net.wolftail.impl.tracker.TrackContainer;
import net.wolftail.impl.util.collect.LinkedObjectCollection;
import net.wolftail.impl.util.collect.SmallShortSet;

@Mixin(Chunk.class)
public abstract class MixinChunk implements ExtTrackerChunk {
	
	@Unique
	private LinkedObjectCollection<Chunk>.Node node;
	
	@Unique
	private TrackContainer<SmallShortSet> cbs;
	
	@Override
	public boolean wolftail_preventUnload() {
		return this.node != null;
	}
	
	@Override
	public void wolftail_blockChanged(short index) {
		
	}
	
	@Override
	public void wolftail_tileEntityChanged(short index) {
		
	}
	
	@Override
	public void wolftail_assemble(int tick) {
		
	}
}
