package net.wolftail.impl.tracker.mixin;

import java.util.IdentityHashMap;
import java.util.function.Consumer;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.wolftail.impl.tracker.ExtTrackerMinecraftServer;
import net.wolftail.impl.tracker.SubscriberWrapper;
import net.wolftail.util.tracker.ContentDiff;
import net.wolftail.util.tracker.ContentTracker;

//anchor of content tracker
@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer implements ExtTrackerMinecraftServer {
	
	@Unique
	private ContentTracker tracker;
	
	@Unique
	private IdentityHashMap<Consumer<ContentDiff>, SubscriberWrapper> wrappers = new IdentityHashMap<>();
	
	@Unique
	private boolean assembling;
	
	@Inject(method = "tick", at = @At(value = "FIELD", target = "tickCounter:I", opcode = Opcodes.PUTFIELD))
	private void onTick(CallbackInfo info) {
		this.assembling = true;
		
		//assemble
		
		
		//dispatch
		this.wrappers.values().forEach(SubscriberWrapper::dispatch);
		
		this.assembling = false;
	}
	
	@Override
	public void wolftail_setTracker(ContentTracker ct) {
		this.tracker = ct;
	}
	
	@Override
	public ContentTracker wolftail_getTracker() {
		return this.tracker;
	}
	
	@Override
	public IdentityHashMap<Consumer<ContentDiff>, SubscriberWrapper> wolftail_wrappers() {
		return this.wrappers;
	}
	
	@Override
	public boolean wolftail_duringAssemble() {
		return this.assembling;
	}
}
