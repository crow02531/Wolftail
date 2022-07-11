package net.wolftail.util.tracker;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.chunk.BlockStateContainer;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.impl.SharedImpls;

//TODO clean and optimize
@SideWith(section = GameSection.GAME_PLAYING)
public final class PartialUniverse {
	
	final Map<DimensionType, PartialWorld> worlds;
	
	public PartialUniverse() {
		this.worlds = new EnumMap<>(DimensionType.class);
	}
	
	public PartialWorld world(@Nonnull DimensionType dim) {
		return this.worlds.get(dim);
	}
	
	@SuppressWarnings("deprecation")
	void accept(PacketBuffer buf) {
		int type = buf.readByte();
		SubscribeOrder order = SharedImpls.H2.readOrder(buf);
		
		PartialWorld w = this.getOrCreate(order.target());
		SlaveChunk c;
		
		if(type == 0) {
			int availableSections = buf.readShort() & 0xFFFF;
			
			c = new SlaveChunk(w, order.chunkX(), order.chunkZ());
			BlockStateContainer[] b = c.blocks;
			
			for(int i = 0; i < 16; ++i) {
				if((availableSections & (1 << i)) != 0)
					(b[i] = new BlockStateContainer()).read(buf);
				else
					b[i] = null;
			}
			
			w.chunks.put(ChunkPos.asLong(order.chunkX(), order.chunkZ()), c);
			
			if(buf.isReadable())
				throw new IllegalArgumentException("buf");
		} else {
			c = w.chunk(order.chunkX(), order.chunkZ());
			
			if(c == null)
				throw new IllegalArgumentException("dst");
			
			int t = 0;
			
			while(buf.isReadable()) {
				c.set(buf.readShort(), Block.BLOCK_STATE_IDS.getByValue(buf.readVarInt()));
				
				if(++t > 64)
					throw new IllegalArgumentException("buf");
			}
		}
	}
	
	PartialWorld getOrCreate(DimensionType d) {
		PartialWorld w = this.worlds.get(d);
		
		if(w == null) 
			this.worlds.put(d, w = new PartialWorld(this, d));
		
		return w;
	}
}
