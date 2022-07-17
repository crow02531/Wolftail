package net.wolftail.util.tracker;

import java.util.Objects;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraft.world.chunk.Chunk;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.impl.SharedImpls;
import net.wolftail.impl.SharedImpls.H2;
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
	
	BLOCK_TILEENTITY {
		
		@Override
		void subscribe(MinecraftServer target, ContentOrder order, H3 subscribeEntry) {
			OrderBlockNormal ord = (OrderBlockNormal) order;
			
			SharedImpls.as(target.getWorld(ord.dim.getId()).getChunkFromBlockCoords(ord.pos)).wolftail_register_BTE(subscribeEntry, H2.toIndex(ord.pos));
		}
		
		@Override
		boolean unsubscribe(MinecraftServer target, ContentOrder order, H6 wrapper) {
			OrderBlockNormal ord = (OrderBlockNormal) order;
			Chunk c = target.getWorld(ord.dim.getId()).getChunkProvider().getLoadedChunk(ord.pos.getX() >> 4, ord.pos.getY() >> 4);
			
			return c == null ? false : SharedImpls.as(c).wolftail_unregister_BTE(wrapper, H2.toIndex(ord.pos));
		}
		
		@Override
		void apply(ByteBuf buf, SlaveUniverse dst) {
			OrderBlockNormal ord = H4.read_BTE(buf);
			BlockPos pos = ord.pos;
			
			dst.jzWorld(ord.dim);
			dst.jzChunk(pos.getX() >> 4, pos.getZ() >> 4);
			
			dst.jzSetTileEntity(pos.getX() & 0xF, pos.getY(), pos.getZ() & 0xF, buf.readBoolean() ? null : H4.readTag(buf));
		}
		
		@Override
		ContentOrder check(ByteBuf buf) {
			OrderBlockNormal ord = H4.read_BTE(buf);
			
			if(!buf.readBoolean())
				H4.readTag(buf);
			
			return ord;
		}
	},
	
	CHUNK_BLOCK {
		
		@Override
		void subscribe(MinecraftServer target, ContentOrder order, H3 subscribeEntry) {
			OrderChunkNormal ord = (OrderChunkNormal) order;
			
			SharedImpls.as(target.getWorld(ord.dim.getId()).getChunkFromChunkCoords(ord.chunkX, ord.chunkZ)).wolftail_register_CB(subscribeEntry);
		}
		
		@Override
		boolean unsubscribe(MinecraftServer target, ContentOrder order, H6 wrapper) {
			OrderChunkNormal ord = (OrderChunkNormal) order;
			Chunk c = target.getWorld(ord.dim.getId()).getChunkProvider().getLoadedChunk(ord.chunkX, ord.chunkZ);
			
			return c == null ? false : SharedImpls.as(c).wolftail_unregister_CB(wrapper);
		}
		
		@SuppressWarnings("deprecation")
		@Override
		void apply(ByteBuf buf, SlaveUniverse dst) {
			OrderChunkNormal ord = H4.read_CB(buf);
			int op = buf.readByte() & 0xFF;
			
			dst.jzWorld(ord.dim);
			dst.jzChunk(ord.chunkX, ord.chunkZ);
			
			if(op == 0) {
				int availableSections;
				
				if((availableSections = buf.readUnsignedShort()) == 0)
					throw new IllegalArgumentException("Illegal availableSections 0");
				
				for(int i = 0; i < 16; ++i)
					dst.jzSetSection(i, (availableSections & (1 << i)) == 0 ? null : buf);
			} else if(0 < op && op <= H4.THRESHOLD_ABANDON) {
				for(; op-- != 0;) {
					short s = buf.readShort();
					
					dst.jzSetBlock(s >> 12 & 0xF, s & 0xFF, s >> 8 & 0xF, Block.BLOCK_STATE_IDS.getByValue(H4.readVarInt(buf)));
				}
			} else throw new IllegalArgumentException("Illegal op " + op);
		}
		
		@SuppressWarnings("deprecation")
		@Override
		ContentOrder check(ByteBuf buf) {
			OrderChunkNormal ord = H4.read_CB(buf);
			int op = buf.readByte() & 0xFF;
			
			if(op == 0) {
				int availableSections;
				
				if((availableSections = buf.readUnsignedShort()) == 0)
					throw new IllegalArgumentException("Illegal availableSections 0");
				
				PacketBuffer wrap = new PacketBuffer(buf);
				
				for(int i = 0; i < 16; ++i) {
					if((availableSections & (1 << i)) != 0)
						new BlockStateContainer().read(wrap);
				}
			} else if(0 < op && op <= H4.THRESHOLD_ABANDON) {
				for(; op-- != 0;) {
					buf.readShort();
					
					Objects.requireNonNull(Block.BLOCK_STATE_IDS.getByValue(H4.readVarInt(buf)));
				}
			} else throw new IllegalArgumentException("Illegal op " + op);
			
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
			dst.jzWorld(H4.read_WW(buf).dim);
			
			dst.jzSetRainingStr(buf.readFloat());
			dst.jzSetThunderingStr(buf.readFloat());
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
			dst.jzWorld(H4.read_WDT(buf).dim);
			
			dst.jzSetDaytime(buf.readLong());
		}
		
		@Override
		ContentOrder check(ByteBuf buf) {
			OrderWorldNormal ord = H4.read_WDT(buf);
			
			buf.readLong();
			
			return ord;
		}
	};
	
	@Nonnull
	public static OrderBlockNormal orderTileEntity(@Nonnull DimensionType dim, @Nonnull BlockPos pos) {
		return new OrderBlockNormal(BLOCK_TILEENTITY, dim, pos);
	}
	
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
	abstract ContentOrder check(ByteBuf buf /*the first varint has read*/); //shouldn't call often
}
