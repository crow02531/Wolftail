package net.wolftail.impl.tracker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.world.chunk.Chunk;
import net.wolftail.impl.tracker.ExtTrackerChunk;
import net.wolftail.impl.util.collect.LinkedObjectCollection;

@Mixin(Chunk.class)
public abstract class MixinChunk implements ExtTrackerChunk {
	
	@Unique
	private LinkedObjectCollection<Chunk>.Node node;
	
	@Override
	public boolean wolftail_preventUnload() {
		return this.node != null;
	}
	
	@Override
	public void wolftail_assemble(int tick) {
		
	}
}
