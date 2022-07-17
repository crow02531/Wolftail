package net.wolftail.impl;

import net.minecraft.world.chunk.Chunk;
import net.wolftail.impl.SharedImpls.H3;
import net.wolftail.impl.SharedImpls.H6;
import net.wolftail.impl.util.collect.LinkedObjectCollection;

public interface ExtensionsWorldServer {
	
	LinkedObjectCollection<Chunk>.Node wolftail_join(Chunk c);
	
	void wolftail_register_WW(H3 subscribeEntry);
	boolean wolftail_unregister_WW(H6 wrapper);
	
	void wolftail_register_WDT(H3 subscribeEntry);
	boolean wolftail_unregister_WDT(H6 wrapper);
	
	void wolftail_postTick(int tick);
}
