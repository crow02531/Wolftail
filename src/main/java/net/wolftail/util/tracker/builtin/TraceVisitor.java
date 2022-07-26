package net.wolftail.util.tracker.builtin;

import java.io.PrintStream;
import java.io.PrintWriter;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.api.lifecycle.SideWith;
import net.wolftail.util.ByteBufs;
import net.wolftail.util.tracker.DiffVisitor;

@SideWith(section = GameSection.GAME_PLAYING)
public final class TraceVisitor implements DiffVisitor {
	
	private final Object lock;
	
	private final PrintWriter pw;
	
	private DiffVisitor dv;
	
	public TraceVisitor(@Nonnull PrintStream ps) {
		this(new PrintWriter(ps, true), null);
	}
	
	public TraceVisitor(@Nonnull PrintWriter pw, DiffVisitor dv) {
		this.lock = new Object();
		
		this.pw = pw;
		this.dv = dv;
	}
	
	@Nonnull
	@Override
	public Object lockObject() {
		return lock;
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
	public void jzBindWorld(@Nonnull ResourceKey<Level> dim) {
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
	public void jzBindSection(int index) {
		pw.print("Bind target section to: ");
		pw.println(index);
		
		if(dv != null)
			dv.jzBindSection(index);
	}
	
	@Override
	public void jzBindBlock(short index) {
		pw.print("Bind target block to: (");
		pw.print(index >> 8 & 0xF);
		pw.print(", ");
		pw.print(index >> 4 & 0xF);
		pw.print(", ");
		pw.print(index & 0xF);
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
	public void jzUnbindSection() {
		pw.print("Unbind target section");
		
		if(dv != null)
			dv.jzUnbindSection();
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
	public void jzSetWeather(float rainLv, float thunderLv) {
		pw.print("Set the weather to: (");
		pw.print(rainLv);
		pw.print(", ");
		pw.print(thunderLv);
		pw.println(")");
		
		if(dv != null)
			dv.jzSetWeather(rainLv, thunderLv);
	}
	
	@Override
	public void jzSetSection(ByteBuf buf) {
		pw.print("Set the block state layer to: ");
		pw.print(buf == null ? "EMPTY" : buf); //TODO print detailed block state layer
		pw.println();
		
		if(dv != null)
			dv.jzSetSection(buf);
	}
	
	@Override
	public void jzSetState(@Nonnull BlockState state) {
		pw.print("Set the block state to: ");
		pw.println(state);
		
		if(dv != null)
			dv.jzSetState(state);
	}
	
	@Override
	public void jzSetTileEntity(ByteBuf buf) {
		pw.print("Set the tile entity to: ");
		pw.println(buf == null ? "EMPTY" : ByteBufs.readTag(buf.duplicate()));
		
		if(dv != null)
			dv.jzSetTileEntity(buf);
	}
}
