package net.wolftail.internal.tracker;

import static net.wolftail.util.MoreByteBufs.readVarInt;

import java.util.Arrays;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.DimensionType;
import net.wolftail.util.tracker.ContentDiff;
import net.wolftail.util.tracker.DiffVisitor;

public final class ImplCD implements ContentDiff, Insncodes {
	
	private final byte[] array;
	
	private int hash;
	
	// callers are responsible of making 'a' safe
	public ImplCD(byte[] a) {
		this.array = a;
	}
	
	@Override
	public void apply(DiffVisitor visitor) {
		apply(Unpooled.wrappedBuffer(this.array), visitor);
	}
	
	@Override
	public void to(ByteBuf dst) {
		dst.writeBytes(this.array);
	}
	
	@Override
	public int hashCode() {
		if (this.hash == 0)
			this.hash = Unpooled.wrappedBuffer(this.array).hashCode();
		
		return this.hash;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null || !(o instanceof ImplCD))
			return false;
		
		ImplCD o0 = (ImplCD) o;
		
		int h = this.hash;
		int h0 = o0.hash;
		
		if (h != h0 && h != 0 && h0 != 0)
			return false;
		
		return Arrays.equals(this.array, o0.array);
	}
	
	public static void check(ByteBuf buf) {
		try {
			apply(buf.duplicate(), new ComplementaryCheckVisitor(buf));
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public static void apply(ByteBuf buf, DiffVisitor visitor) {
		visitor.jzBegin();
		
		while (buf.isReadable()) {
			switch (buf.readByte()) {
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
				visitor.jzSetState(read_state(buf));
				
				break;
			case BAS_BLOCK_TILEENTITY:
				visitor.jzBindBlock(buf.readShort());
				set_tileentity(buf, visitor);
				
				break;
			case SET_STATE:
				visitor.jzSetState(read_state(buf));
				
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
		
		for (int i = 0; i < 16; ++i)
			v.jzSetSection(i, (availableSections & (1 << i)) == 0 ? null : buf.readSlice(readVarInt(buf)));
	}
	
	private static void bulk_bas_block_state(ByteBuf buf, DiffVisitor v) {
		for (int num = buf.readUnsignedShort() + 1; num-- != 0;) {
			v.jzBindBlock(buf.readShort());
			v.jzSetState(read_state(buf));
		}
	}
	
	private static void bulk_bas_block_tileentity(ByteBuf buf, DiffVisitor v) {
		for (int num = buf.readUnsignedShort() + 1; num-- != 0;) {
			v.jzBindBlock(buf.readShort());
			set_tileentity(buf, v);
		}
	}
	
	@SuppressWarnings("deprecation")
	private static IBlockState read_state(ByteBuf buf) {
		return Block.BLOCK_STATE_IDS.getByValue(readVarInt(buf));
	}
}
