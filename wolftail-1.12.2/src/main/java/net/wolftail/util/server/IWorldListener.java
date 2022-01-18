package net.wolftail.util.server;

import net.minecraft.world.WorldServer;

public interface IWorldListener {
	
	void onInitial(WorldServer world, Object padding_data);
	
	void onChanged(WorldServer world, Object padding_data);
	
	void onFinal(WorldServer world);
	
	void onInitial(WorldServer world, int chunkX, int chunkY, Object padding_data);
	
	void onChanged(WorldServer world, int chunkX, int chunkY, Object padding_data);
	
	void onFinal(WorldServer world, int chunkX, int chunkY);
}
