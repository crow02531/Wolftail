package net.wolftail.impl.tracker;

import static net.wolftail.impl.util.MoreByteBuf.readVarInt;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.DimensionType;
import net.wolftail.util.tracker.ContentDiff;
import net.wolftail.util.tracker.DiffVisitor;

public final class ImplCD implements ContentDiff, Insncodes {
	
	private final ByteBuf buf;
	
	//callers are responsible of making the buf safe
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
		visitor.jzBegin();
		
		while(buf.isReadable()) {
			switch(buf.readByte()) {
			case BIND_WORLD:
				visitor.jzBindWorld(DimensionType.getById(readVarInt(buf)));
				
				break;
			case BAS_WORLD_DAYTIME:
				visitor.jzBindWorld(DimensionType.getById(readVarInt(buf)));
				visitor.jzSetDaytime(buf.readLong());
				
				break;
			case BAS_WORLD_WEATHER:
				visitor.jzBindWorld(DimensionType.getById(readVarInt(buf)));
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
			case SET_SECTION:
				set_section(buf, visitor);
				
				break;
			case BULK_SET_SECTION:
				bulk_set_section(buf, visitor);
				
				break;
			case BIND_BLOCK:
				visitor.jzBindBlock(buf.readShort());
				
				break;
			case BAS_BLOCK_STATE:
				visitor.jzBindBlock(buf.readShort());
				visitor.jzSetState(readBlockState(buf));
				
				break;
			case BAS_BLOCK_TILEENTITY:
				visitor.jzBindBlock(buf.readShort());
				set_tileentity(buf, visitor);
				
				break;
			case SET_STATE:
				visitor.jzSetState(readBlockState(buf));
				
				break;
			case SET_TILEENTITY:
				set_tileentity(buf, visitor);
				
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
	
	private static void bind_chunk(ByteBuf buf, DiffVisitor v) {
		int s0 = buf.readUnsignedShort();
		int s1 = buf.readUnsignedShort();
		int s2 = buf.readUnsignedShort();
		
		v.jzBindChunk(((s1 & 0x3F) << 16 | s0) - 1875000, ((s2 << 16 | s1) >> 6) - 1875000);
	}
	
	private static void set_section(ByteBuf buf, DiffVisitor v) {
		int i = buf.readByte();
		
		v.jzSetSection(i & 0xF, (i & 0xF0) == 0 ? null : buf.readSlice(readVarInt(buf)));
	}
	
	private static void set_tileentity(ByteBuf buf, DiffVisitor v) {
		int size = readVarInt(buf);
		
		v.jzSetTileEntity(size == 0 ? null : buf.readSlice(size));
	}
	
	private static void bulk_set_section(ByteBuf buf, DiffVisitor v) {
		int availableSections = buf.readUnsignedShort();

		for(int i = 0; i < 16; ++i)
			v.jzSetSection(i, (availableSections & (1 << i)) == 0 ? null : buf.readSlice(readVarInt(buf)));
	}
	
	private static void bulk_bas_block_state(ByteBuf buf, DiffVisitor v) {
		for(int num = buf.readUnsignedShort() + 1; num-- != 0;) {
			v.jzBindBlock(buf.readShort());
			v.jzSetState(readBlockState(buf));
		}
	}
	
	private static void bulk_bas_block_tileentity(ByteBuf buf, DiffVisitor v) {
		for(int num = buf.readUnsignedShort() + 1; num-- != 0;) {
			v.jzBindBlock(buf.readShort());
			set_tileentity(buf, v);
		}
	}
	
	@SuppressWarnings("deprecation")
	private static IBlockState readBlockState(ByteBuf buf) {
		return Block.BLOCK_STATE_IDS.getByValue(readVarInt(buf));
	}
}
