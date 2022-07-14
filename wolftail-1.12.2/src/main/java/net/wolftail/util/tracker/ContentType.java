package net.wolftail.util.tracker;

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
import net.wolftail.impl.SharedImpls.H6;

/**
 * The type of an order.
 * 
 * @see ContentOrder
 */
@SideWith(section = GameSection.GAME_PLAYING)
public enum ContentType {
	
	CHUNK_BLOCK {
		
		@Override
		void subscribe(MinecraftServer target, ContentOrder order, H3 subscribeEntry) {
			OrderChunkNormal order0 = (OrderChunkNormal) order;
			
			SharedImpls.as(target.getWorld(order0.dim.getId()).getChunkFromChunkCoords(order0.chunkX, order0.chunkZ)).wolftail_register_CB(subscribeEntry);
		}
		
		@Override
		boolean unsubscribe(MinecraftServer target, ContentOrder order, H6 wrapper) {
			OrderChunkNormal order0 = (OrderChunkNormal) order;
			Chunk chunk = target.getWorld(order0.dim.getId()).getChunkProvider().getLoadedChunk(order0.chunkX, order0.chunkZ);
			
			return chunk == null ? false : SharedImpls.as(chunk).wolftail_unregister_CB(wrapper);
		}
		
		@SuppressWarnings("deprecation")
		@Override
		void apply(ByteBuf buf, SlaveUniverse dst) {
			OrderChunkNormal order = H4.read_CB(buf);
			int op = buf.readByte();
			
			SlaveWorld w = dst.goc_world(order.dim);
			SlaveChunk c;
			
			if(op == 0) {
				int availableSections;
				
				if((availableSections = buf.readUnsignedShort()) == 0)
					throw new IllegalArgumentException();
				
				c = new SlaveChunk(w, order.chunkX, order.chunkZ);
				PacketBuffer wrap = new PacketBuffer(buf);
				
				for(int i = 0; i < 16; ++i) {
					if((availableSections & (1 << i)) != 0)
						(c.blocks[i] = new BlockStateContainer()).read(wrap);
					else
						c.blocks[i] = null;
				}
				
				w.chunks.put(ChunkPos.asLong(order.chunkX, order.chunkZ), c);
			} else if(0 < op && op <= 64) {
				c = w.chunk(order.chunkX(), order.chunkZ());
				
				for(; op-- != 0;)
					c.set(buf.readShort(), Block.BLOCK_STATE_IDS.getByValue(H4.readVarInt(buf)));
			} else throw new IllegalArgumentException();
		}
		
		@SuppressWarnings("deprecation")
		@Override
		ContentOrder check(ByteBuf buf) {
			OrderChunkNormal ord = H4.read_CB(buf);
			int op = buf.readByte();
			
			if(op == 0) {
				int availableSections;
				
				if((availableSections = buf.readUnsignedShort()) == 0)
					throw new IllegalArgumentException();
				
				PacketBuffer wrap = new PacketBuffer(buf);
				
				for(int i = 0; i < 16; ++i) {
					if((availableSections & (1 << i)) != 0)
						new BlockStateContainer().read(wrap);
				}
			} else if(0 < op && op <= 64) {
				for(; op-- != 0;) {
					buf.readShort();
					
					if(Block.BLOCK_STATE_IDS.getByValue(H4.readVarInt(buf)) == null)
						throw new IllegalArgumentException();
				}
			} else throw new IllegalArgumentException();
			
			return ord;
		}
	},
	
	WORLD_WEATHER {
		
		@Override
		void subscribe(MinecraftServer target, ContentOrder order, H3 subscribeEntry) {
			SharedImpls.as(target.getWorld(((OrderWorldNormal) order).dim.getId())).wolftail_register_WW(subscribeEntry);
		}
		
		@Override
		boolean unsubscribe(MinecraftServer target, ContentOrder order, H6 wrapper) {
			return SharedImpls.as(target.getWorld(((OrderWorldNormal) order).dim.getId())).wolftail_unregister_WW(wrapper);
		}
		
		@Override
		void apply(ByteBuf buf, SlaveUniverse dst) {
			SlaveWeather w = dst.goc_world(H4.read_WW(buf).dim).goc_weather();
			
			w.rainingStrength = buf.readFloat();
			w.thunderingStrength = buf.readFloat();
		}
		
		@Override
		ContentOrder check(ByteBuf buf) {
			OrderWorldNormal ord = H4.read_WW(buf);
			
			buf.readLong();
			
			return ord;
		}
	},
	
	WORLD_DAYTIME {
		
		@Override
		void subscribe(MinecraftServer target, ContentOrder order, H3 subscribeEntry) {
			SharedImpls.as(target.getWorld(((OrderWorldNormal) order).dim.getId())).wolftail_register_WDT(subscribeEntry);
		}
		
		@Override
		boolean unsubscribe(MinecraftServer target, ContentOrder order, H6 wrapper) {
			return SharedImpls.as(target.getWorld(((OrderWorldNormal) order).dim.getId())).wolftail_unregister_WDT(wrapper);
		}
		
		@Override
		void apply(ByteBuf buf, SlaveUniverse dst) {
			SlaveTime t = dst.goc_world(H4.read_WDT(buf).dim).goc_time();
			
			t.dayTime = buf.readLong();
		}
		
		@Override
		ContentOrder check(ByteBuf buf) {
			OrderWorldNormal ord = H4.read_WDT(buf);
			
			buf.readLong();
			
			return ord;
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
	
	abstract void subscribe(MinecraftServer target, ContentOrder order, H3 subscribeEntry);
	abstract boolean unsubscribe(MinecraftServer target, ContentOrder order, H6 wrapper);
	
	abstract void apply(ByteBuf buf /*the first varint has read*/, SlaveUniverse dst);
	abstract ContentOrder check(ByteBuf buf /*the first varint has read*/);
}
