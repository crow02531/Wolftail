package com.example.examplemod;

import java.util.LinkedList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.wolftail.api.INetworkHandler;
import net.wolftail.api.IServerHandler;
import net.wolftail.api.PlayContext;
import net.wolftail.util.MoreServers;
import net.wolftail.util.tracker.DiffWriter;
import net.wolftail.util.tracker.Timing;
import net.wolftail.util.tracker.builtin.OrderDaytime;
import net.wolftail.util.tracker.builtin.OrderWeather;

public final class PigServerHandler implements IServerHandler {

	static final PigServerHandler INSTANCE = new PigServerHandler();

	private final List<PigServerNetHandler> handlers;

	private PigServerHandler() {
		MinecraftForge.EVENT_BUS.register(this);

		this.handlers = new LinkedList<>();
	}

	@SubscribeEvent
	public void on_server_post_tick(TickEvent.ServerTickEvent e) {
		this.handlers.forEach(PigServerNetHandler::update);
	}

	@Override
	public void handleEnter(PlayContext context) {
		PigServerNetHandler h = new PigServerNetHandler(context);

		context.setHandler(h);
		handlers.add(h);
	}

	@Override
	public void handleLeave(PlayContext context) {
		PigServerNetHandler h = (PigServerNetHandler) context.getHandler();

		h.quit();
		handlers.remove(h);
	}

	private static class PigServerNetHandler implements INetworkHandler {

		final PlayContext context;

		final ByteBuf receiver;
		final DiffWriter acceptor;

		PigServerNetHandler(PlayContext c) {
			this.context = c;
			(acceptor = new DiffWriter()).setOutput(receiver = c.alloc().buffer());

			MinecraftServer ms = MoreServers.serverInstance();
			new OrderDaytime(DimensionType.OVERWORLD).track(ms, acceptor, Timing.of(10));
			new OrderWeather(DimensionType.OVERWORLD).track(ms, acceptor, Timing.EVERY_TICK);
		}

		@Override
		public void handle(ByteBuf buf) {
		}

		public void update() {
			context.send(receiver.copy());
			receiver.clear();
		}

		public void quit() {
			MinecraftServer ms = MoreServers.serverInstance();
			new OrderDaytime(DimensionType.OVERWORLD).untrack(ms, acceptor);
			new OrderWeather(DimensionType.OVERWORLD).untrack(ms, acceptor);
		}
	}
}
