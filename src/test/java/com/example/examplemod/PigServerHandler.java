package com.example.examplemod;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.DimensionType;
import net.wolftail.api.INetworkHandler;
import net.wolftail.api.IServerHandler;
import net.wolftail.api.PlayContext;
import net.wolftail.api.lifecycle.GameSection;
import net.wolftail.util.tracker.Timing;
import net.wolftail.util.tracker.builtin.OrderDaytime;
import net.wolftail.util.tracker.builtin.TraceVisitor;

public final class PigServerHandler implements IServerHandler {
	
	static final PigServerHandler INSTANCE = new PigServerHandler();
	
	private PigServerHandler() {
	}
	
	@Override
	public void handleEnter(PlayContext context) {
		context.setHandler(new PigServerNetHandler(context));
	}
	
	@Override
	public void handleLeave(PlayContext context) {
		
	}
	
	private static class PigServerNetHandler implements INetworkHandler {
		
		final PlayContext playContext;
		
		PigServerNetHandler(PlayContext c) {
			this.playContext = c;
			
			(new OrderDaytime(DimensionType.OVERWORLD)).track(GameSection.serverInstance(),
					new TraceVisitor(System.out), Timing.of(10));
		}
		
		@Override
		public void handle(ByteBuf buf) {
			
		}
		
		@Override
		public void tick() {
			
		}
	}
}
