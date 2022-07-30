package com.example.examplemod;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.wolftail.api.INetworkHandler;
import net.wolftail.api.IServerHandler;
import net.wolftail.api.PlayContext;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.util.tracker.Timing;
import net.wolftail.util.tracker.builtin.OrderDaytime;
import net.wolftail.util.tracker.builtin.OrderTileEntity;
import net.wolftail.util.tracker.builtin.TraceVisitor;

public final class PigServerHandler implements IServerHandler {
	
	static final PigServerHandler INSTANCE = new PigServerHandler();
	
	private PigServerHandler() {
	}
	
	@Override
	public void handleEnter(PlayContext context) {
		context.setHandler(new PigServerNetHandler());
	}
	
	@Override
	public void handleLeave(PlayContext context) {
		
	}
	
	private static class PigServerNetHandler implements INetworkHandler {
		
		PigServerNetHandler() {
			TraceVisitor v = new TraceVisitor(System.out);
			
			(new OrderDaytime(DimensionType.OVERWORLD)).track(GameSection.serverInstance(), v, Timing.of(10));
			(new OrderTileEntity(DimensionType.OVERWORLD, new BlockPos(0, 4, 0))).track(GameSection.serverInstance(), v,
					Timing.EVERY_TICK);
		}
		
		@Override
		public void handle(ByteBuf buf) {
			
		}
		
		@Override
		public void tick() {
			
		}
	}
}
