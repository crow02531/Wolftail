package net.wolftail.impl.mixin;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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
import net.wolftail.util.tracker.ContentDiff;
import net.wolftail.util.tracker.ContentType;
import net.wolftail.util.tracker.OrderWorldWeather;

//ContentTracker Supporter
//ticking subscribed chunks and adding an IWorldEventListener
//send weather changes to subscribers
@Mixin(WorldServer.class)
public abstract class MixinWorldServer implements ExtensionsWorldServer {
	
	@Unique
	private Set<H3> subscribers = new HashSet<>();
	
	@Unique
	private ExtensionsChunk head;
	
	@Inject(method = "init", at = @At("RETURN"))
	private void onInit(CallbackInfoReturnable<World> info) {
		((WorldServer) SharedImpls.as(this)).addEventListener(new ServerWorldListener());
	}
	
	@Inject(method = "tick", at = @At(value = "INVOKE_STRING", target = "endStartSection(Ljava/lang/String;)V", args = "ldc=chunkMap", shift = Shift.AFTER))
	private void onTick(CallbackInfo info) {
		ExtensionsChunk c = this.head;
		
		while(c != null) {
			c.wolftail_tick();
			
			c = c.wolftail_getNext();
		}
	}
	
	@Inject(method = "updateWeather", at = @At(value = "INVOKE", target = "updateWeather()V", shift = Shift.AFTER))
	private void onUpdateWeather(CallbackInfo info) {
		if(this.subscribers.isEmpty()) return;
		
		WorldServer w = (WorldServer) SharedImpls.as(this);
		OrderWorldWeather order = ContentType.orderWeather(w.provider.getDimensionType());
		
		ImplCD init = null;
		ImplCD diff = null;
		
		for(H3 e : this.subscribers) {
			if(e.initial) {
				if(init == null) {
					if(diff == null)
						diff = new ImplCD(order, H4.make_WW(order, w.rainingStrength, w.thunderingStrength));
					
					init = diff;
				}
				
				e.subscriber.accept(init);
				e.initial = false;
			} else {
				if(diff == null) {
					if(w.rainingStrength != w.prevRainingStrength || w.thunderingStrength != w.prevThunderingStrength) {
						if(init == null)
							init = new ImplCD(order, H4.make_WW(order, w.rainingStrength, w.thunderingStrength));
						
						diff = init;
					}
				}
				
				if(diff != null)
					e.subscriber.accept(diff);
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
	public void wolftail_register(Consumer<ContentDiff> subscriber) {
		if(!this.subscribers.add(new H3(subscriber)))
			throw new IllegalArgumentException();
	}
	
	@Override
	public void wolftail_unregister(Consumer<ContentDiff> subscriber) {
		this.subscribers.remove(new H3(subscriber));
	}
}
