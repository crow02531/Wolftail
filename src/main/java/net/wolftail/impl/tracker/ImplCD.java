package net.wolftail.impl.tracker;

import static net.wolftail.util.ByteBufs.readResourceLocation;
import static net.wolftail.util.ByteBufs.readVarInt;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.wolftail.util.tracker.ContentDiff;
import net.wolftail.util.tracker.DiffVisitor;

public final class ImplCD implements ContentDiff, Insncodes {
	
	private final ByteBuf buf;
	
	//caller is responsible of making the buf safe
	public ImplCD(ByteBuf buf) {
		this.buf = buf;
	}
	
	@Override
	public ByteBuf toByteBuf() {
		return this.buf.duplicate();
	}
	
	@Override
	public void apply(DiffVisitor visitor) {
		apply(this.toByteBuf(), visitor);
	}
	
	@Override
	public int hashCode() {
		return this.buf.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(o == null || !(o instanceof ImplCD)) return false;
		
		return ((ImplCD) o).buf.equals(this.buf);
	}
	
	public static void apply(ByteBuf buf, DiffVisitor visitor) {
		synchronized(visitor.lockObject()) {
			visitor.jzBegin();
			
			while(buf.isReadable()) {
				switch(buf.readByte()) {
				case BIND_WORLD:
					visitor.jzBindWorld(readD(buf));
					
					break;
				case BAS_WORLD_DAYTIME:
					visitor.jzBindWorld(readD(buf));
					visitor.jzSetDaytime(buf.readLong());
					
					break;
				case BAS_WORLD_WEATHER:
					visitor.jzBindWorld(readD(buf));
					visitor.jzSetWeather(buf.readFloat(), buf.readFloat());
					
					break;
				case SET_DAYTIME:
					visitor.jzSetDaytime(buf.readLong());
					
					break;
				case SET_WEATHER:
					visitor.jzSetWeather(buf.readFloat(), buf.readFloat());
					
					break;
				case BIND_CHUNK:
					bind_chunk(buf, visitor);
					
					break;
				case BIND_SECTION:
					visitor.jzBindSection(readVarInt(buf));
					
					break;
				case SET_SECTION:
					visitor.jzSetSection(readNullOrNonEmptyBuf(buf));
					
					break;
				case BIND_BLOCK:
					visitor.jzBindBlock(buf.readShort());
					
					break;
				case BAS_BLOCK_STATE:
					visitor.jzBindBlock(buf.readShort());
					visitor.jzSetState(readBS(buf));
					
					break;
				case BAS_BLOCK_TILEENTITY:
					visitor.jzBindBlock(buf.readShort());
					visitor.jzSetTileEntity(readNullOrNonEmptyBuf(buf));
					
					break;
				case SET_STATE:
					visitor.jzSetState(readBS(buf));
					
					break;
				case SET_TILEENTITY:
					visitor.jzSetTileEntity(readNullOrNonEmptyBuf(buf));
					
					break;
				case BULK_BAS_BLOCK_STATE:
					bulk_bas_block_state(buf, visitor);
					
					break;
				case BULK_BAS_BLOCK_TILEENTITY:
					bulk_bas_block_tileentity(buf, visitor);
					
					break;
				default:
					throw new Error();
				}
			}
			
			visitor.jzEnd();
		}
	}
	
	private static void bind_chunk(ByteBuf buf, DiffVisitor v) {
		int s0 = buf.readUnsignedShort();
		int s1 = buf.readUnsignedShort();
		int s2 = buf.readUnsignedShort();
		
		v.jzBindChunk(((s1 & 0x3F) << 16 | s0) - 1875000, ((s2 << 16 | s1) >> 6) - 1875000);
	}
	
	private static void bulk_bas_block_state(ByteBuf buf, DiffVisitor v) {
		for(int num = buf.readUnsignedShort() + 1; num-- != 0;) {
			v.jzBindBlock(buf.readShort());
			v.jzSetState(readBS(buf));
		}
	}
	
	private static void bulk_bas_block_tileentity(ByteBuf buf, DiffVisitor v) {
		for(int num = buf.readUnsignedShort() + 1; num-- != 0;) {
			v.jzBindBlock(buf.readShort());
			v.jzSetTileEntity(readNullOrNonEmptyBuf(buf));
		}
	}
	
	private static BlockState readBS(ByteBuf buf) {
		return Block.BLOCK_STATE_REGISTRY.byId(readVarInt(buf));
	}
	
	private static ResourceKey<Level> readD(ByteBuf buf) {
		return ResourceKey.create(Registry.DIMENSION_REGISTRY, readResourceLocation(buf));
	}
	
	private static ByteBuf readNullOrNonEmptyBuf(ByteBuf buf) {
		int size = readVarInt(buf);
		
		return size == 0 ? null : buf.readSlice(size);
	}
}
