package net.wolftail.impl;

import net.wolftail.impl.SharedImpls.H3;
import net.wolftail.impl.SharedImpls.H6;

public interface ExtensionsWorldServer {
	
	ExtensionsChunk wolftail_getHead();
	void wolftail_setHead(ExtensionsChunk h);
	
	void wolftail_register_WW(H3 subscribeEntry);
	boolean wolftail_unregister_WW(H6 wrapper);
	
	void wolftail_register_WDT(H3 subscribeEntry);
	boolean wolftail_unregister_WDT(H6 wrapper);
	
	void wolftail_postTick(int tick);
}
