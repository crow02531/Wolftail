package net.wolftail.util.tracker;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.chunk.BlockStateContainer;
import net.wolftail.impl.SharedImpls;

//TODO getBlockState outbound check
//TODO remove method missing
//TODO optimize
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
			c = new SlaveChunk(w, order.chunkX(), order.chunkZ());
			
			BlockStateContainer[] b = c.blocks;
			for(int i = 0; i < 16; ++i)
				b[i].read(buf);
			
			long index = ChunkPos.asLong(order.chunkX(), order.chunkZ());
			
			if(w.chunks.get(index) != null)
				throw new IllegalArgumentException("dst");
			w.chunks.put(index, c);
			
			if(buf.readableBytes() != 0)
				throw new IllegalArgumentException("buf");
		} else {
			c = w.chunk(order.chunkX(), order.chunkZ());
			
			if(c == null)
				throw new IllegalArgumentException("dst");
			
			int t = 0;
			
			while(buf.readableBytes() != 0) {
				c.set(buf.readShort(), Block.BLOCK_STATE_IDS.getByValue(buf.readVarInt()));
				
				if(++t > 65536)
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
