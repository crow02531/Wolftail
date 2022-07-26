package net.wolftail.impl.tracker;

public interface Insncodes {
	
	byte BIND_WORLD = 0,
			BIND_CHUNK = 1,
			BIND_SECTION = 2,
			BIND_BLOCK = 3;
	
	byte SET_DAYTIME = 4,
			SET_WEATHER = 5,
			SET_SECTION = 6,
			SET_STATE = 7,
			SET_TILEENTITY = 8;
	
	byte BAS_WORLD_DAYTIME = 9,
			BAS_WORLD_WEATHER = 10,
			BAS_BLOCK_STATE = 11,
			BAS_BLOCK_TILEENTITY = 12;
	
	byte BULK_BAS_BLOCK_STATE = 13,
			BULK_BAS_BLOCK_TILEENTITY = 14;
}
