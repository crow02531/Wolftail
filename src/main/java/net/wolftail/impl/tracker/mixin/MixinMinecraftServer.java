package net.wolftail.impl.tracker.mixin;

import java.util.IdentityHashMap;
import java.util.function.Consumer;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.wolftail.impl.tracker.ExtTrackerWorldServer;
import net.wolftail.impl.tracker.SubscriberWrapper;
import net.wolftail.util.tracker.ContentDiff;
import net.wolftail.util.tracker.ContentOrder;
import net.wolftail.util.tracker.ContentTracker;
import net.wolftail.util.tracker.Timing;

//anchor of content tracker
@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer implements ContentTracker {
	
	@Shadow
	public WorldServer[] worlds;
	
	@Shadow
	public int tickCounter;
	
	@Unique
	private IdentityHashMap<Consumer<ContentDiff>, SubscriberWrapper> wrappers = new IdentityHashMap<>();
	
	@Unique
	private boolean assembling;
	
	@Inject(method = "tick", at = @At(value = "FIELD", target = "tickCounter:I", opcode = Opcodes.PUTFIELD))
	private void on_tick_putField_tickCounter(CallbackInfo info) {
		this.assembling = true;
		
		//assemble
		for(WorldServer w : this.worlds)
			((ExtTrackerWorldServer) w).wolftail_assemble(this.tickCounter);
		
		//dispatch
		this.wrappers.values().forEach(SubscriberWrapper::dispatch);
		
		this.assembling = false;
	}
	
	@Override
	public boolean subscribe(ContentOrder order, Consumer<ContentDiff> subscriber, Timing timing) {
		SubscriberWrapper w = this.wrappers.get(subscriber);
		if(w == null) w = new SubscriberWrapper(subscriber);
		
		if(order.track((MinecraftServer) (Object) this, w.getWriter(), timing)) {
			this.wrappers.put(subscriber, w);
			w.onSubscribe();
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean unsubscribe(ContentOrder order, Consumer<ContentDiff> subscriber) {
		SubscriberWrapper w = this.wrappers.get(subscriber);
		
		if(w != null && order.untrack((MinecraftServer) (Object) this, w.getWriter())) {
			if(w.onUnsubscribe())
				this.wrappers.remove(subscriber);
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean inAssemble() {
		return this.assembling;
	}
}
