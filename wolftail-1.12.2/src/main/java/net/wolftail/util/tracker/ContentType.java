package net.wolftail.util.tracker;

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
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.impl.SharedImpls;
import net.wolftail.impl.SharedImpls.H4;

@SideWith(section = GameSection.GAME_PLAYING)
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
	},
	
	WORLD_WEATHER {
		
		@Override
		ContentOrder read(ByteBuf src) {
			return H4.read_WW(src);
		}
		
		@Override
		void subscribe(MinecraftServer target, ContentOrder order, Consumer<ContentDiff> subscriber) {
			SharedImpls.as(target.getWorld(((OrderWorldWeather) order).dim.getId())).wolftail_register(subscriber);
		}
		
		@Override
		void unsubscribe(MinecraftServer target, ContentOrder order, Consumer<ContentDiff> subscriber) {
			SharedImpls.as(target.getWorld(((OrderWorldWeather) order).dim.getId())).wolftail_unregister(subscriber);
		}
		
		@Override
		void apply(PacketBuffer buf, SlaveUniverse dst) {
			SlaveWorld w = dst.getOrCreate(H4.read_WW(buf).dim);
			
			w.rainingStrength = buf.readFloat();
			w.thunderingStrength = buf.readFloat();
			
			if(buf.isReadable())
				throw new IllegalArgumentException();
		}
	};
	
	@Nonnull
	public static OrderChunkBlock orderBlock(@Nonnull DimensionType dim, int chunkX, int chunkZ) {
		return new OrderChunkBlock(dim, chunkX, chunkZ);
	}
	
	@Nonnull
	public static OrderWorldWeather orderWeather(@Nonnull DimensionType dim) {
		return new OrderWorldWeather(dim);
	}
	
	abstract ContentOrder read(ByteBuf src);
	
	abstract void subscribe(MinecraftServer target, ContentOrder order, Consumer<ContentDiff> subscriber);
	abstract void unsubscribe(MinecraftServer target, ContentOrder order, Consumer<ContentDiff> subscriber);
	
	abstract void apply(PacketBuffer buf, SlaveUniverse dst);
}
