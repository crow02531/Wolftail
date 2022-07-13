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
import net.wolftail.impl.SharedImpls.H3;
import net.wolftail.impl.SharedImpls.H4;

@SideWith(section = GameSection.GAME_PLAYING)
public enum ContentType {
	
	CHUNK_BLOCK {
		
		@Override
		ContentOrder read(ByteBuf src) {
			return H4.read_CB(src);
		}

		@Override
		void subscribe(MinecraftServer target, ContentOrder order, H3 subscribeEntry) {
			OrderChunkNormal order0 = (OrderChunkNormal) order;
			
			SharedImpls.as(target.getWorld(order0.dim.getId()).getChunkFromChunkCoords(order0.chunkX, order0.chunkZ)).wolftail_register_CB(subscribeEntry);
		}

		@Override
		void unsubscribe(MinecraftServer target, ContentOrder order, Consumer<ContentDiff> subscriber) {
			OrderChunkNormal order0 = (OrderChunkNormal) order;
			Chunk chunk = target.getWorld(order0.dim.getId()).getChunkProvider().getLoadedChunk(order0.chunkX, order0.chunkZ);
			
			if(chunk != null) SharedImpls.as(chunk).wolftail_unregister_CB(subscriber);
		}
		
		@SuppressWarnings("deprecation")
		@Override
		void apply(PacketBuffer buf, SlaveUniverse dst) {
			OrderChunkNormal order = H4.read_CB(buf);
			
			SlaveWorld w = dst.goc_world(order.dim);
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
				
				ensureReadAll(buf);
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
		void subscribe(MinecraftServer target, ContentOrder order, H3 subscribeEntry) {
			SharedImpls.as(target.getWorld(((OrderWorldNormal) order).dim.getId())).wolftail_register_WW(subscribeEntry);
		}
		
		@Override
		void unsubscribe(MinecraftServer target, ContentOrder order, Consumer<ContentDiff> subscriber) {
			SharedImpls.as(target.getWorld(((OrderWorldNormal) order).dim.getId())).wolftail_unregister_WW(subscriber);
		}
		
		@Override
		void apply(PacketBuffer buf, SlaveUniverse dst) {
			SlaveWeather w = dst.goc_world(H4.read_WW(buf).dim).goc_weather();
			
			w.rainingStrength = buf.readFloat();
			w.thunderingStrength = buf.readFloat();
			
			ensureReadAll(buf);
		}
	},
	
	WORLD_DAYTIME {
		
		@Override
		ContentOrder read(ByteBuf src) {
			return H4.read_WDT(null);
		}
		
		@Override
		void subscribe(MinecraftServer target, ContentOrder order, H3 subscribeEntry) {
			SharedImpls.as(target.getWorld(((OrderWorldNormal) order).dim.getId())).wolftail_register_WDT(subscribeEntry);
		}
		
		@Override
		void unsubscribe(MinecraftServer target, ContentOrder order, Consumer<ContentDiff> subscriber) {
			SharedImpls.as(target.getWorld(((OrderWorldNormal) order).dim.getId())).wolftail_unregister_WDT(subscriber);
		}
		
		@Override
		void apply(PacketBuffer buf, SlaveUniverse dst) {
			SlaveTime t = dst.goc_world(H4.read_WDT(buf).dim).goc_time();
			
			t.dayTime = buf.readLong();
			
			ensureReadAll(buf);
		}
	};
	
	@Nonnull
	public static OrderChunkNormal orderBlock(@Nonnull DimensionType dim, int chunkX, int chunkZ) {
		return new OrderChunkNormal(CHUNK_BLOCK, dim, chunkX, chunkZ);
	}
	
	@Nonnull
	public static OrderWorldNormal orderWeather(@Nonnull DimensionType dim) {
		return new OrderWorldNormal(WORLD_WEATHER, dim);
	}
	
	@Nonnull
	public static OrderWorldNormal orderDaytime(@Nonnull DimensionType dim) {
		return new OrderWorldNormal(WORLD_DAYTIME, dim);
	}
	
	abstract ContentOrder read(ByteBuf src);
	
	abstract void subscribe(MinecraftServer target, ContentOrder order, H3 subscribeEntry);
	abstract void unsubscribe(MinecraftServer target, ContentOrder order, Consumer<ContentDiff> subscriber);
	
	abstract void apply(PacketBuffer buf, SlaveUniverse dst);
	
	private static void ensureReadAll(ByteBuf buf) {
		if(buf.isReadable()) throw new IllegalArgumentException();
	}
}
