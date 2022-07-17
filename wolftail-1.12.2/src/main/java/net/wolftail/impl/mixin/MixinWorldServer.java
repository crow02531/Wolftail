package net.wolftail.impl.mixin;

import java.util.HashMap;
import java.util.HashSet;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.wolftail.impl.ExtensionsWorldServer;
import net.wolftail.impl.ServerWorldListener;
import net.wolftail.impl.SharedImpls;
import net.wolftail.impl.SharedImpls.H3;
import net.wolftail.impl.SharedImpls.H4;
import net.wolftail.impl.SharedImpls.H5;
import net.wolftail.impl.SharedImpls.H6;
import net.wolftail.impl.util.collect.LinkedObjectCollection;
import net.wolftail.impl.util.collect.SmallLong2ObjectMap;
import net.wolftail.util.tracker.ContentType;
import net.wolftail.util.tracker.OrderWorldNormal;

//ContentTracker Supporter
@Mixin(WorldServer.class)
public abstract class MixinWorldServer implements ExtensionsWorldServer {
	
	@Unique
	private HashMap<H3, H3> subscribers_WW = new HashMap<>();
	
	@Unique
	private HashSet<H3> subscribers_WDT = new HashSet<>();
	
	@Unique
	private SmallLong2ObjectMap<H5> prevWeathers = new SmallLong2ObjectMap<>(10);
	
	@Unique
	private LinkedObjectCollection<Chunk> subscribedChunks = new LinkedObjectCollection<>();
	
	@Inject(method = "init", at = @At("RETURN"))
	private void onInit(CallbackInfoReturnable<World> info) {
		((WorldServer) SharedImpls.as(this)).addEventListener(new ServerWorldListener());
	}
	
	@Unique
	private void postTick_C(int tick) {
		for(Chunk c : this.subscribedChunks)
			SharedImpls.as(c).wolftail_postTick(tick);
	}
	
	@Unique
	private void postTick_WW(int tick) {
		WorldServer w = (WorldServer) SharedImpls.as(this);
		SmallLong2ObjectMap<H5> prevWeathers = this.prevWeathers;
		
		OrderWorldNormal order = ContentType.orderWeather(w.provider.getDimensionType());
		ByteBuf sent = null;
		
		for(int i = prevWeathers.size(); i-- != 0;)
			prevWeathers.getVal(i).bool = false;
		
		for(H3 e : this.subscribers_WW.keySet()) {
			if(e.initial) {
				if(sent == null) sent = H4.make_WW(order, w.rainingStrength, w.thunderingStrength);
				
				if(e.getInterval() <= 20 && !prevWeathers.containsKey(e.tickSequence))
					prevWeathers.put(e.tickSequence, new H5(w.rainingStrength, w.thunderingStrength));
				
				e.wrapper.cumulate(order, sent);
				e.initial = false;
			} else if(e.shouldSend(tick)) {
				H5 prev = prevWeathers.get(e.tickSequence);
				
				if(prev == null || prev.bool || !prev.equals(w.rainingStrength, w.thunderingStrength)) {
					if(sent == null) sent = H4.make_WW(order, w.rainingStrength, w.thunderingStrength);
					
					e.wrapper.cumulate(order, sent);
					
					if(prev != null && !prev.bool) {
						prev.bool = true;
						prev.set(w.rainingStrength, w.thunderingStrength);
					}
				}
			}
		}
	}
	
	@Unique
	private void postTick_WDT(int tick) {
		OrderWorldNormal order = null;
		ByteBuf sent = null;
		
		for(H3 e : this.subscribers_WDT) {
			if(e.shouldSend(tick)) {
				if(order == null) {
					WorldServer w = (WorldServer) SharedImpls.as(this);
					
					order = ContentType.orderDaytime(w.provider.getDimensionType());
					sent = H4.make_WDT(order, w);
				}
				
				e.wrapper.cumulate(order, sent);
			}
		}
	}
	
	@Override
	public LinkedObjectCollection<Chunk>.Node wolftail_join(Chunk c) {
		return this.subscribedChunks.enter(c);
	}
	
	@Override
	public void wolftail_register_WW(H3 subscribeEntry) {
		if(this.subscribers_WW.putIfAbsent(subscribeEntry, subscribeEntry) != null)
			throw new IllegalArgumentException();
	}
	
	@Override
	public boolean wolftail_unregister_WW(H6 wrapper) {
		H3 entry = this.subscribers_WW.remove(new H3(wrapper));
		
		if(entry != null) {
			for(H3 e : this.subscribers_WW.keySet()) {
				if(e.tickSequence == entry.tickSequence)
					return true;
			}
			
			if(entry.getInterval() <= 20) this.prevWeathers.remove(entry.tickSequence);
			return true;
		}
		
		return false;
	}
	
	@Override
	public void wolftail_register_WDT(H3 subscribeEntry) {
		if(!this.subscribers_WDT.add(subscribeEntry))
			throw new IllegalArgumentException();
	}
	
	@Override
	public boolean wolftail_unregister_WDT(H6 wrapper) {
		return this.subscribers_WDT.remove(new H3(wrapper));
	}
	
	@Override
	public void wolftail_postTick(int tick) {
		this.postTick_C(tick);
		
		if(!this.subscribers_WW.isEmpty()) this.postTick_WW(tick);
		if(!this.subscribers_WDT.isEmpty()) this.postTick_WDT(tick);
	}
}
