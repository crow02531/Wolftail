package net.wolftail.util.tracker.builtin;

import java.io.PrintStream;
import java.io.PrintWriter;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.DimensionType;
import net.wolftail.util.tracker.SlaveUniverse;

public class TraceUniverse extends SlaveUniverse {
	
	protected final PrintWriter pw;
	
	public TraceUniverse(@Nonnull PrintStream ps) {
		this(new PrintWriter(ps, true));
	}
	
	public TraceUniverse(@Nonnull PrintWriter pw) {
		this.pw = pw;
	}
	
	@Override
	protected void jzBegin() {
		this.pw.println("------------------Begin------------------");
		this.pw.print("Processing Thread: ");
		this.pw.println(Thread.currentThread());
	}
	
	@Override
	protected void jzEnd() {
		this.pw.println("-------------------End-------------------");
	}
	
	@Override
	protected void jzWorld(DimensionType dim) {
		this.pw.print("Change target dimension to: ");
		this.pw.println(dim);
	}
	
	@Override
	protected void jzChunk(int chunkX, int chunkZ) {
		this.pw.print("Change target chunk to: (");
		this.pw.print(chunkX);
		this.pw.print(", ");
		this.pw.print(chunkZ);
		this.pw.println(")");
	}
	
	@Override
	protected void jzSetDaytime(long daytime) {
		this.pw.print("Set the daytime: ");
		this.pw.println(daytime);
	}
	
	@Override
	protected void jzSetRainingStr(float str) {
		this.pw.print("Set the raining strength: ");
		this.pw.println(str);
	}
	
	@Override
	protected void jzSetThunderingStr(float str) {
		this.pw.print("Set the thundering strength: ");
		this.pw.println(str);
	}
	
	@Override
	protected void jzSetBlock(int localX, int localY, int localZ, IBlockState state) {
		this.pw.print("Set the block state: (");
		this.pw.print(localX);
		this.pw.print(", ");
		this.pw.print(localY);
		this.pw.print(", ");
		this.pw.print(localZ);
		this.pw.print(") -> ");
		this.pw.println(state);
	}
	
	@Override
	protected void jzSetSection(int index, ByteBuf buf) {
		this.pw.print("Set the chunk section(block state layer): (");
		this.pw.print(index);
		this.pw.println(") ->");
		this.pw.println(buf == null ? "EMPTY" : ByteBufUtil.prettyHexDump(buf));
	}
	
	@Override
	protected void jzSetTileEntity(int localX, int localY, int localZ, NBTTagCompound serialized) {
		this.pw.print("Set the tile entity: (");
		this.pw.print(localX);
		this.pw.print(", ");
		this.pw.print(localY);
		this.pw.print(", ");
		this.pw.print(localZ);
		this.pw.println(") ->");
		this.pw.println(serialized == null ? "EMPTY" : serialized);
	}
}
