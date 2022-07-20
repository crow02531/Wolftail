package net.wolftail.impl.tracker;

public interface Insncodes {
	
	byte BIND_WORLD = 0,
			BIND_CHUNK = 1,
			BIND_BLOCK = 2;
	
	byte SET_DAYTIME = 3,
			SET_WEATHER = 4,
			SET_SECTION = 5,
			SET_STATE = 6,
			SET_TILEENTITY = 7;
	
	byte BAS_WORLD_DAYTIME = 8,
			BAS_WORLD_WEATHER = 9,
			BAS_BLOCK_STATE = 10,
			BAS_BLOCK_TILEENTITY = 11;
	
	byte BULK_BAS_BLOCK_STATE = 12,
			BULK_BAS_BLOCK_TILEENTITY = 13,
			BULK_SET_SECTION = 14;
}
