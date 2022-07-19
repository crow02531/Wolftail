package net.wolftail.impl.tracker;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static net.wolftail.impl.tracker.Insncodes.readTag;
import static net.wolftail.impl.tracker.Insncodes.readVarInt;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.DimensionType;
import net.minecraft.world.chunk.BlockStateContainer;
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
		apply(this.toByteBuf(), visitor, true);
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
	
	public static void apply(ByteBuf buf, DiffVisitor visitor, boolean ensureSafe) {
		synchronized(visitor.lockObject()) {
			visitor.jzBegin();
			
			boolean bind_world = false;
			boolean bind_chunk = false;
			boolean bind_block = false;
			
			while(buf.isReadable()) {
				switch(buf.readByte()) {
				case BIND_WORLD:
					visitor.jzBindWorld(DimensionType.getById(readVarInt(buf)));
					
					bind_world = true;
					bind_chunk = false;
					bind_block = false;
					break;
				case BAS_WORLD_DAYTIME:
					visitor.jzBindWorld(DimensionType.getById(readVarInt(buf)));
					visitor.jzSetDaytime(buf.readLong());
					
					bind_world = true;
					bind_chunk = false;
					bind_block = false;
					break;
				case BAS_WORLD_WEATHER:
					visitor.jzBindWorld(DimensionType.getById(readVarInt(buf)));
					visitor.jzSetWeather(buf.readFloat(), buf.readFloat());
					
					bind_world = true;
					bind_chunk = false;
					bind_block = false;
					break;
				case SET_DAYTIME:
					checkState(bind_world);
					visitor.jzSetDaytime(buf.readLong());
					
					break;
				case SET_WEATHER:
					checkState(bind_world);
					visitor.jzSetWeather(buf.readFloat(), buf.readFloat());
					
					break;
				case BIND_CHUNK:
					checkState(bind_world);
					bind_chunk(buf, visitor, ensureSafe);
					
					bind_chunk = true;
					break;
				case SET_SECTION:
					checkState(bind_chunk);
					set_section(buf, visitor);
					
					break;
				case BULK_SET_SECTION:
					checkState(bind_chunk);
					bulk_set_section(buf, visitor);
					
					break;
				case BIND_BLOCK:
					checkState(bind_chunk);
					visitor.jzBindBlock(buf.readShort());
					
					bind_block = true;
					break;
				case BAS_BLOCK_STATE:
					checkState(bind_chunk);
					visitor.jzBindBlock(buf.readShort());
					visitor.jzSetState(readBS(buf));
					
					bind_block = true;
					break;
				case BAS_BLOCK_TILEENTITY:
					checkState(bind_chunk);
					visitor.jzBindBlock(buf.readShort());
					visitor.jzSetTileEntity(buf.readBoolean() ? readTag(buf) : null);
					
					bind_block = true;
					break;
				case SET_STATE:
					checkState(bind_block);
					visitor.jzSetState(readBS(buf));
					
					break;
				case SET_TILEENTITY:
					checkState(bind_block);
					visitor.jzSetTileEntity(buf.readBoolean() ? readTag(buf) : null);
					
					break;
				case BULK_BAS_BLOCK_STATE:
					checkState(bind_chunk);
					bulk_bas_block_state(buf, visitor);
					
					bind_block = true;
					break;
				case BULK_BAS_BLOCK_TILEENTITY:
					checkState(bind_chunk);
					bulk_bas_block_tileentity(buf, visitor);
					
					bind_block = true;
					break;
				default:
					throw new IllegalArgumentException();
				}
			}
			
			visitor.jzEnd();
		}
	}
	
	private static void bind_chunk(ByteBuf buf, DiffVisitor v, boolean ensureSafe) {
		int s0 = buf.readUnsignedShort();
		int s1 = buf.readUnsignedShort();
		int s2 = buf.readUnsignedShort();
		
		int x = ((s1 & 0x3F) << 16 | s0) - 1875000;
		int z = ((s2 << 16 | s1) >> 6) - 1875000;
		
		if(ensureSafe)
			v.jzBindChunk(x, z);
		else if(!(-1875000 <= x && x < 1875000 &&
				-1875000 <= z && z < 1875000 &&
				(s2 & 0xF000) == 0)) {
			throw new IllegalArgumentException();
		}
	}
	
	private static void set_section(ByteBuf buf, DiffVisitor v) {
		int i = buf.readByte();
		BlockStateContainer l;
		
		if((i & 0xF0) == 0) l = null;
		else l = readBSL(new PacketBuffer(buf));
		
		v.jzSetSection(i & 0xF, l);
	}
	
	private static void bulk_set_section(ByteBuf buf, DiffVisitor v) {
		int availableSections = buf.readUnsignedShort();
		PacketBuffer wrap = new PacketBuffer(buf);
		
		for(int i = 0; i < 16; ++i)
			v.jzSetSection(i, (availableSections & (1 << i)) == 0 ? null : readBSL(wrap));
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
			v.jzSetTileEntity(buf.readBoolean() ? readTag(buf) : null);
		}
	}
	
	private static BlockStateContainer readBSL(PacketBuffer wrap) {
		BlockStateContainer l = new BlockStateContainer();
		l.read(wrap);
		
		return l;
	}
	
	@SuppressWarnings("deprecation")
	private static IBlockState readBS(ByteBuf buf) {
		return checkNotNull(Block.BLOCK_STATE_IDS.getByValue(readVarInt(buf)));
	}
}
