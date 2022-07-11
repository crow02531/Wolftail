package net.wolftail.util.tracker;

import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraft.world.chunk.Chunk;
import net.wolftail.impl.SharedImpls;
import net.wolftail.impl.SharedImpls.H4;

public enum ContentType {
	
	CHUNK_BLOCK {
		
		@Override
		ContentOrder read(ByteBuf src) {
			return H4.read_CB(src);
		}

		@Override
		void subscribe(MinecraftServer target, ContentOrder order, Consumer<ContentDiff> subscriber) {
			OrderChunkBlock order0 = (OrderChunkBlock) order;
			
			SharedImpls.as(target.getWorld(order0.dim.getId()).getChunkFromChunkCoords(order0.chunkX, order0.chunkZ)).wolftail_register(subscriber);
		}

		@Override
		void unsubscribe(MinecraftServer target, ContentOrder order, Consumer<ContentDiff> subscriber) {
			OrderChunkBlock order0 = (OrderChunkBlock) order;
			Chunk chunk = target.getWorld(order0.dim.getId()).getChunkProvider().getLoadedChunk(order0.chunkX, order0.chunkZ);
			
			if(chunk != null) SharedImpls.as(chunk).wolftail_unregister(subscriber);
		}
		
		@SuppressWarnings("deprecation")
		@Override
		void apply(PacketBuffer buf, SlaveUniverse dst) {
			OrderChunkBlock order = H4.read_CB(buf);
			
			SlaveWorld w = dst.getOrCreate(order.dim);
			SlaveChunk c;
			
			if(buf.readByte() == 0) {
				int availableSections = buf.readUnsignedShort();
				
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
					throw new IllegalArgumentException();
			} else {
				c = w.chunk(order.chunkX(), order.chunkZ());
				
				int t = 0;
				
				while(buf.isReadable()) {
					c.set(buf.readShort(), Block.BLOCK_STATE_IDS.getByValue(buf.readVarInt()));
					
					if(++t > 64)
						throw new IllegalArgumentException();
				}
				
				if(t == 0)
					throw new IllegalArgumentException();
			}
		}
	};
	
	@Nonnull
	public static OrderChunkBlock orderBlock(@Nonnull DimensionType dim, int chunkX, int chunkZ) {
		return new OrderChunkBlock(Objects.requireNonNull(dim), chunkX, chunkZ);
	}
	
	abstract ContentOrder read(ByteBuf src);
	
	abstract void subscribe(MinecraftServer target, ContentOrder order, Consumer<ContentDiff> subscriber);
	abstract void unsubscribe(MinecraftServer target, ContentOrder order, Consumer<ContentDiff> subscriber);
	
	abstract void apply(PacketBuffer buf, SlaveUniverse dst);
}
