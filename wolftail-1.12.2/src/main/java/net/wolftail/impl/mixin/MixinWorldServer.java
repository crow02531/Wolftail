package net.wolftail.impl.mixin;

import java.util.HashMap;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.wolftail.impl.ExtensionsChunk;
import net.wolftail.impl.ExtensionsWorldServer;
import net.wolftail.impl.ImplCD;
import net.wolftail.impl.ServerWorldListener;
import net.wolftail.impl.SharedImpls;
import net.wolftail.impl.SharedImpls.H3;
import net.wolftail.impl.SharedImpls.H4;
import net.wolftail.impl.SharedImpls.H5;
import net.wolftail.impl.SmallLong2ObjectMap;
import net.wolftail.util.tracker.ContentDiff;
import net.wolftail.util.tracker.ContentType;
import net.wolftail.util.tracker.OrderWorldNormal;

//ContentTracker Supporter
@Mixin(WorldServer.class)
public abstract class MixinWorldServer implements ExtensionsWorldServer {
	
	@Unique
	private HashMap<H3, H3> subscribers_WW = new HashMap<>();
	
	@Unique
	private HashMap<H3, H3> subscribers_WDT = new HashMap<>();
	
	@Unique
	private SmallLong2ObjectMap<H5> prevWeathers = new SmallLong2ObjectMap<>(3);
	
	@Unique
	private ExtensionsChunk head;
	
	@Inject(method = "init", at = @At("RETURN"))
	private void onInit(CallbackInfoReturnable<World> info) {
		((WorldServer) SharedImpls.as(this)).addEventListener(new ServerWorldListener());
	}
	
	@Unique
	private void postTick_C(int tick) {
		ExtensionsChunk c = this.head;
		
		while(c != null) {
			c.wolftail_postTick(tick);
			
			c = c.wolftail_getNext();
		}
	}
	
	@Unique
	private void postTick_WW(int tick) {
		WorldServer w = (WorldServer) SharedImpls.as(this);
		OrderWorldNormal order = ContentType.orderWeather(w.provider.getDimensionType());
		SmallLong2ObjectMap<H5> prevWeathers = this.prevWeathers;
		
		ImplCD sent = null;
		
		for(int i = prevWeathers.size(); i-- != 0;)
			prevWeathers.getVal(i).bool = false;
		
		for(H3 e : this.subscribers_WW.keySet()) {
			if(e.initial) {
				if(sent == null)
					sent = new ImplCD(order, H4.make_WW(order, w.rainingStrength, w.thunderingStrength));
				
				if(!prevWeathers.containsKey(e.tickSequence))
					prevWeathers.put(e.tickSequence, new H5(w.rainingStrength, w.thunderingStrength));
				
				e.subscriber.accept(sent);
				e.initial = false;
			} else if(e.shouldSend(tick)) {
				H5 prev = prevWeathers.get(e.tickSequence);
				
				if(prev.bool || !prev.equals(w.rainingStrength, w.thunderingStrength)) {
					if(sent == null)
						sent = new ImplCD(order, H4.make_WW(order, w.rainingStrength, w.thunderingStrength));
					
					e.subscriber.accept(sent);
					
					if(!prev.bool) {
						prev.bool = true;
						
						prev.set(w.rainingStrength, w.thunderingStrength);
					}
				}
			}
		}
	}
	
	@Unique
	private void postTick_WDT(int tick) {
		ImplCD sent = null;
		
		for(H3 e : this.subscribers_WDT.keySet()) {
			if(e.shouldSend(tick)) {
				if(sent == null) {
					WorldServer w = (WorldServer) SharedImpls.as(this);
					OrderWorldNormal order = ContentType.orderDaytime(w.provider.getDimensionType());
					
					sent = new ImplCD(order, H4.make_WDT(order, w));
				}
				
				e.subscriber.accept(sent);
			}
		}
	}
	
	@Override
	public ExtensionsChunk wolftail_getHead() {
		return this.head;
	}
	
	@Override
	public void wolftail_setHead(ExtensionsChunk h) {
		this.head = h;
	}
	
	@Override
	public void wolftail_register_WW(H3 subscribeEntry) {
		if(this.subscribers_WW.putIfAbsent(subscribeEntry, subscribeEntry) != null)
			throw new IllegalArgumentException();
	}
	
	@Override
	public void wolftail_unregister_WW(Consumer<ContentDiff> subscriber) {
		H3 entry = this.subscribers_WW.remove(new H3(subscriber));
		
		if(entry != null) {
			for(H3 e : this.subscribers_WW.keySet()) {
				if(e.tickSequence == entry.tickSequence)
					return;
			}
			
			this.prevWeathers.remove(entry.tickSequence);
		}
	}
	
	@Override
	public void wolftail_register_WDT(H3 subscribeEntry) {
		if(this.subscribers_WDT.putIfAbsent(subscribeEntry, subscribeEntry) != null)
			throw new IllegalArgumentException();
	}
	
	@Override
	public void wolftail_unregister_WDT(Consumer<ContentDiff> subscriber) {
		this.subscribers_WDT.remove(new H3(subscriber));
	}
	
	@Override
	public void wolftail_postTick(int tick) {
		this.postTick_C(tick);
		
		if(!this.subscribers_WW.isEmpty()) this.postTick_WW(tick);
		if(!this.subscribers_WDT.isEmpty()) this.postTick_WDT(tick);
	}
}
