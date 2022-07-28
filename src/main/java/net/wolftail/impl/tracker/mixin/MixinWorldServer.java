package net.wolftail.impl.tracker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.wolftail.impl.tracker.ExtTrackerChunk;
import net.wolftail.impl.tracker.ExtTrackerWorldServer;
import net.wolftail.impl.tracker.TrackContainer;
import net.wolftail.impl.tracker.WorldServerListener;
import net.wolftail.impl.util.collect.LinkedObjectCollection;
import net.wolftail.util.tracker.DiffVisitor;
import net.wolftail.util.tracker.Timing;

//anchor of WDT; arrange subscribed chunks
@Mixin(WorldServer.class)
public abstract class MixinWorldServer extends World implements ExtTrackerWorldServer {

	@Unique
	private final TrackContainer<Void> wdt = new TrackContainer<>(3, null);
	
	@Unique
	private final LinkedObjectCollection<Chunk> chunks = new LinkedObjectCollection<>();
	
	//unused
	protected MixinWorldServer(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn,
			Profiler profilerIn, boolean client) {
		super(saveHandlerIn, info, providerIn, profilerIn, client);
	}
	
	@Inject(method = "init", at = @At("RETURN"))
	private void on_init_return(CallbackInfoReturnable<World> cir) {
		this.addEventListener(new WorldServerListener());
	}
	
	@Override
	public boolean wolftail_wdt_track(DiffVisitor acceptor, Timing timing) {
		return this.wdt.add(timing, acceptor);
	}
	
	@Override
	public boolean wolftail_wdt_untrack(DiffVisitor acceptor) {
		return this.wdt.remove(acceptor);
	}
	
	@Override
	public void wolftail_assemble(int tick) {
		this.wdt.forEach(tick, r -> {
			DiffVisitor v = r.getMultiA();
			
			v.jzBegin();
			v.jzBindWorld(this.provider.getDimensionType());
			v.jzSetDaytime(this.worldInfo.getWorldTime());
			v.jzEnd();
		});
		
		this.chunks.forEach(c -> ((ExtTrackerChunk) c).wolftail_assemble(tick));
	}
	
	@Override
	public LinkedObjectCollection<Chunk>.Node wolftail_join(Chunk c) {
		return this.chunks.enter(c);
	}
}
