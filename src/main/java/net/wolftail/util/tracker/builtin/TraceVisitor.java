package net.wolftail.util.tracker.builtin;

import java.io.PrintStream;
import java.io.PrintWriter;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.DimensionType;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.impl.util.MoreByteBuf;
import net.wolftail.util.tracker.DiffVisitor;

@SideWith(section = GameSection.GAME_PLAYING)
public final class TraceVisitor implements DiffVisitor {
	
	private final PrintWriter pw;
	
	private DiffVisitor dv;
	
	public TraceVisitor(@Nonnull PrintStream ps) {
		this(new PrintWriter(ps, true), null);
	}
	
	public TraceVisitor(@Nonnull PrintWriter pw, DiffVisitor dv) {
		this.pw = pw;
		this.dv = dv;
	}
	
	@Override
	public void jzBegin() {
		pw.println("------------------Begin------------------");
		pw.print("Processing Thread: ");
		pw.println(Thread.currentThread());
		
		if(dv != null)
			dv.jzBegin();
	}
	
	@Override
	public void jzEnd() {
		pw.println("-------------------End-------------------");
		
		if(dv != null)
			dv.jzEnd();
	}
	
	@Override
	public void jzBindWorld(@Nonnull DimensionType dim) {
		pw.print("Bind target world to: ");
		pw.println(dim);
		
		if(dv != null)
			dv.jzBindWorld(dim);
	}
	
	@Override
	public void jzBindChunk(int chunkX, int chunkZ) {
		pw.print("Bind target chunk to: (");
		pw.print(chunkX);
		pw.print(", ");
		pw.print(chunkZ);
		pw.println(")");
		
		if(dv != null)
			dv.jzBindChunk(chunkX, chunkZ);
	}
	
	@Override
	public void jzBindBlock(short index) {
		pw.print("Bind target block to: (");
		pw.print(index >> 12 & 0xF);
		pw.print(", ");
		pw.print(index & 0xFF);
		pw.print(", ");
		pw.print(index >> 8 & 0xF);
		pw.println(")");
		
		if(dv != null)
			dv.jzBindBlock(index);
	}
	
	@Override
	public void jzUnbindWorld() {
		pw.print("Unbind target world");
		
		if(dv != null)
			dv.jzUnbindWorld();
	}
	
	@Override
	public void jzUnbindChunk() {
		pw.print("Unbind target chunk");
		
		if(dv != null)
			dv.jzUnbindChunk();
	}
	
	@Override
	public void jzUnbindBlock() {
		pw.print("Unbind target block");
		
		if(dv != null)
			dv.jzUnbindBlock();
	}
	
	@Override
	public void jzSetDaytime(long daytime) {
		pw.print("Set the daytime to: ");
		pw.println(daytime);
		
		if(dv != null)
			dv.jzSetDaytime(daytime);
	}
	
	@Override
	public void jzSetWeather(float rainStr, float thunderStr) {
		pw.print("Set the weather to: (");
		pw.print(rainStr);
		pw.print(", ");
		pw.print(thunderStr);
		pw.println(")");
		
		if(dv != null)
			dv.jzSetWeather(rainStr, thunderStr);
	}
	
	@Override
	public void jzSetSection(int index, ByteBuf buf) {
		pw.print("Set the block state layer of section ");
		pw.print(index);
		pw.print(" to: ");
		pw.print(buf == null ? "EMPTY" : buf); //TODO print detailed block state layer
		pw.println();
		
		if(dv != null)
			dv.jzSetSection(index, buf);
	}
	
	@Override
	public void jzSetState(@Nonnull IBlockState state) {
		pw.print("Set the block state to: ");
		pw.println(state);
		
		if(dv != null)
			dv.jzSetState(state);
	}
	
	@Override
	public void jzSetTileEntity(ByteBuf buf) {
		pw.print("Set the tile entity to: ");
		pw.println(buf == null ? "EMPTY" : MoreByteBuf.readTag(buf.duplicate()));
		
		if(dv != null)
			dv.jzSetTileEntity(buf);
	}
}
