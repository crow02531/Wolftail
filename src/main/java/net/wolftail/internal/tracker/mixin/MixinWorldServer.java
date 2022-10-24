package net.wolftail.internal.tracker.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.wolftail.internal.tracker.ExtTrackerChunk;
import net.wolftail.internal.tracker.ExtTrackerWorldServer;
import net.wolftail.internal.tracker.WorldServerListener;
import net.wolftail.internal.tracker.container.TimedTrackComplex;
import net.wolftail.internal.util.collect.LinkedObjectCollection;
import net.wolftail.util.tracker.DiffVisitor;
import net.wolftail.util.tracker.Timing;

//anchor of WDT; arrange subscribed chunks
@Mixin(WorldServer.class)
public abstract class MixinWorldServer extends World implements ExtTrackerWorldServer {
	
	@Final
	@Shadow
	public MinecraftServer mcServer;
	
	@Unique
	private final TimedTrackComplex<Void> wdt = new TimedTrackComplex<>(3, null);
	
	@Unique
	private final TimedTrackComplex<float[]> ww = new TimedTrackComplex<>(3, () -> new float[2]);
	
	@Unique
	private final LinkedObjectCollection<Chunk> chunks = new LinkedObjectCollection<>();
	
	// unused
	protected MixinWorldServer(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn,
			Profiler profilerIn, boolean client) {
		super(saveHandlerIn, info, providerIn, profilerIn, client);
	}
	
	@Inject(method = "init", at = @At("RETURN"))
	private void on_init_return(CallbackInfoReturnable<World> cir) {
		this.addEventListener(new WorldServerListener());
	}
	
	@Unique
	private int tickCounter() {
		return this.mcServer.getTickCounter();
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
	public boolean wolftail_ww_track(DiffVisitor acceptor, Timing timing) {
		return this.ww.add(timing, acceptor);
	}
	
	@Override
	public boolean wolftail_ww_untrack(DiffVisitor acceptor) {
		return this.ww.remove(acceptor);
	}
	
	@Override
	public void wolftail_wdt_assemble() {
		this.wdt.forEach(this.tickCounter(), r -> {
			DiffVisitor v = r.getMultiA();
			
			v.jzBegin();
			v.jzBindWorld(this.provider.getDimensionType());
			v.jzSetDaytime((int) (this.worldInfo.getWorldTime() % 24000));
			v.jzEnd();
		});
	}
	
	@Override
	public void wolftail_ww_assemble() {
		this.ww.forEach(this.tickCounter(), r -> {
			float[] cache = r.attachment;
			DiffVisitor v;
			
			if ((v = r.getMultiA()) != null) {
				v.jzBegin();
				v.jzBindWorld(this.provider.getDimensionType());
				v.jzSetWeather(this.rainingStrength, this.thunderingStrength);
				v.jzEnd();
			}
			
			if ((v = r.getMultiB()) != null) {
				if (cache[0] != this.rainingStrength || cache[1] != this.thunderingStrength) {
					v.jzBegin();
					v.jzBindWorld(this.provider.getDimensionType());
					v.jzSetWeather(this.rainingStrength, this.thunderingStrength);
					v.jzEnd();
				}
			}
			
			cache[0] = this.rainingStrength;
			cache[1] = this.thunderingStrength;
			
			r.transferA2B();
		});
	}
	
	@Override
	public void wolftail_cbs_assemble() {
		this.chunks.forEach(c -> ((ExtTrackerChunk) c).wolftail_cbs_assemble(this.tickCounter()));
	}
	
	@Override
	public void wolftail_bte_assemble() {
		this.chunks.forEach(c -> ((ExtTrackerChunk) c).wolftail_bte_assemble(this.tickCounter()));
	}
	
	@Override
	public LinkedObjectCollection<Chunk>.Node wolftail_join(Chunk c) {
		return this.chunks.enter(c);
	}
}
