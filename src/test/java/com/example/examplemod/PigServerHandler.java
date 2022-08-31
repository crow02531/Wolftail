package com.example.examplemod;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.wolftail.api.INetworkHandler;
import net.wolftail.api.IServerHandler;
import net.wolftail.api.PlayContext;
import net.wolftail.util.MoreServers;
import net.wolftail.util.tracker.ContentOrder;
import net.wolftail.util.tracker.DiffVisitor;
import net.wolftail.util.tracker.Timing;
import net.wolftail.util.tracker.builtin.OrderDaytime;
import net.wolftail.util.tracker.builtin.OrderTileEntity;
import net.wolftail.util.tracker.builtin.OrderWeather;
import net.wolftail.util.tracker.builtin.TraceVisitor;

public final class PigServerHandler implements IServerHandler {
	
	static final PigServerHandler INSTANCE = new PigServerHandler();
	
	private static final ContentOrder O0 = new OrderDaytime(DimensionType.OVERWORLD);
	private static final ContentOrder O1 = new OrderWeather(DimensionType.OVERWORLD);
	private static final ContentOrder O2 = new OrderTileEntity(DimensionType.OVERWORLD, new BlockPos(0, 4, 0));
	
	private PigServerHandler() {
	}
	
	@Override
	public void handleEnter(PlayContext context) {
		context.setHandler(new PigServerNetHandler());
	}
	
	@Override
	public void handleLeave(PlayContext context) {
		DiffVisitor dv = ((PigServerNetHandler) context.getHandler()).v;
		
		O0.untrack(MoreServers.serverInstance(), dv);
		O1.untrack(MoreServers.serverInstance(), dv);
		O2.untrack(MoreServers.serverInstance(), dv);
	}
	
	private static class PigServerNetHandler implements INetworkHandler {
		
		final TraceVisitor v = new TraceVisitor(System.out);
		
		PigServerNetHandler() {
			O0.track(MoreServers.serverInstance(), this.v, Timing.of(10));
			O1.track(MoreServers.serverInstance(), this.v, Timing.of(2));
			O2.track(MoreServers.serverInstance(), this.v, Timing.EVERY_TICK);
		}
		
		@Override
		public void handle(ByteBuf buf) {
			buf.readByte();
		}
		
		@Override
		public void tick() {
			
		}
	}
}
