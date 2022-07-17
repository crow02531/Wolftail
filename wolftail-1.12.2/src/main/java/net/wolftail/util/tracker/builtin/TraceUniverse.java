package net.wolftail.util.tracker.builtin;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.UUID;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
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
		pw.println("------------------Begin------------------");
		pw.print("Processing Thread: ");
		pw.println(Thread.currentThread());
	}
	
	@Override
	protected void jzEnd() {
		pw.println("-------------------End-------------------");
	}
	
	@Override
	protected void jzBindWorld(DimensionType dim) {
		if(dim == null) {
			pw.println("Unbind dimension");
		} else {
			pw.print("Bind target dimension to: ");
			pw.println(dim);
		}
	}
	
	@Override
	protected void jzSetDaytime(long daytime) {
		pw.print("Set the daytime: ");
		pw.println(daytime);
	}
	
	@Override
	protected void jzSetRainingStr(float str) {
		pw.print("Set the raining strength: ");
		pw.println(str);
	}
	
	@Override
	protected void jzSetThunderingStr(float str) {
		pw.print("Set the thundering strength: ");
		pw.println(str);
	}
	
	@Override
	protected void jzBindChunk(ChunkPos chunkPos) {
		if(chunkPos == null) {
			pw.println("Unbind chunk");
		} else jzBindChunk(chunkPos.x, chunkPos.z);
	}
	
	@Override
	protected void jzBindChunk(int chunkX, int chunkZ) {
		pw.print("Bind target chunk to: (");
		pw.print(chunkX);
		pw.print(", ");
		pw.print(chunkZ);
		pw.println(")");
	}
	
	@Override
	protected void jzSetBlock(int localX, int localY, int localZ, IBlockState state) {
		pw.print("Set the block state: (");
		pw.print(localX);
		pw.print(", ");
		pw.print(localY);
		pw.print(", ");
		pw.print(localZ);
		pw.print(") -> ");
		pw.println(state);
	}
	
	@Override
	protected void jzSetSection(int index, ByteBuf buf) {
		pw.print("Set the chunk section[block state layer]: (");
		pw.print(index);
		pw.println(") ->");
		pw.println(buf == null ? "EMPTY" : ByteBufUtil.prettyHexDump(buf));
	}
	
	@Override
	protected void jzSetTileEntity(int localX, int localY, int localZ, NBTTagCompound serialized) {
		pw.print("Set the tile entity: (");
		pw.print(localX);
		pw.print(", ");
		pw.print(localY);
		pw.print(", ");
		pw.print(localZ);
		pw.println(") ->");
		pw.println(serialized == null ? "EMPTY" : serialized);
	}
	
	@Override
	protected void jzGenEntity(int entityID, UUID uniqueID, Class<? extends Entity> type) {
		pw.print("Generate new entity[");
		pw.print(type);
		pw.print("]: tid=");
		pw.print(entityID);
		pw.print(", uid=");
		pw.println(uniqueID);
	}
	
	@Override
	protected void jzDeleteEntity(int entityID) {
		pw.print("Delete entity: tid=");
		pw.println(entityID);
	}
	
	@Override
	protected void jzBindEntity(int entityID) {
		pw.print("Bind target entity to: tid=");
		pw.println(entityID);
	}
	
	@Override
	protected void jzSetEntityShape(float width, float height) {
		pw.print("Set the entity shape: (");
		pw.print(width);
		pw.print(", ");
		pw.print(height);
		pw.println(")");
	}
	
	@Override
	protected void jzSetEntityPosition(double x, double y, double z) {
		pw.print("Set the entity position: (");
		pw.print(x);
		pw.print(", ");
		pw.print(y);
		pw.print(", ");
		pw.print(z);
		pw.println(")");
	}
	
	@Override
	protected void jzSetEntityRotation(float yaw, float pitch) {
		pw.print("Set the entity rotation: (");
		pw.print(yaw);
		pw.print(", ");
		pw.print(pitch);
		pw.println(")");
	}
}
