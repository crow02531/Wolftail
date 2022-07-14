package net.wolftail.impl.mixin;

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
import net.wolftail.api.lifecycle.PhysicalType;
import net.wolftail.impl.ExtensionsMinecraftServer;
import net.wolftail.impl.ImplMPCRoot;
import net.wolftail.impl.SharedImpls;
import net.wolftail.impl.SharedImpls.H6;
import net.wolftail.util.tracker.ContentDiff;
import net.wolftail.util.tracker.ContentTracker;

//rootPlayContextManager, content tracker addition
//collect and send all content diffs to subscribers
@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer implements ExtensionsMinecraftServer {
	
	@Shadow
	public WorldServer[] worlds;
	
	@Shadow
	private int tickCounter;
	
	@Unique
	protected ImplMPCRoot root;
	
	@Unique
	private ContentTracker tracker;
	
	@Unique
	private IdentityHashMap<Consumer<ContentDiff>, H6> wrappers = new IdentityHashMap<>();
	
	@Unique
	private boolean sending;
	
	@Inject(method = "<init>", at = @At("RETURN"))
	private void onConstruct(CallbackInfo info) {
		if(PhysicalType.INTEGRATED_CLIENT.is())
			this.root = new ImplMPCRoot(SharedImpls.as(this));
	}
	
	@Inject(method = "tick", at = @At(value = "FIELD", target = "tickCounter:I", opcode = Opcodes.PUTFIELD))
	private void onTick(CallbackInfo info) {
		this.sending = true;
		
		for(WorldServer w : this.worlds)
			SharedImpls.as(w).wolftail_postTick(this.tickCounter);
		
		this.wrappers.values().forEach(H6::flush);
		
		this.sending = false;
	}
	
	@Override
	public ImplMPCRoot wolftail_getRootManager() {
		return this.root;
	}
	
	@Override
	public ContentTracker wolftail_getContentTracker() {
		return this.tracker;
	}
	
	@Override
	public void wolftail_setContentTracker(ContentTracker obj) {
		this.tracker = obj;
	}
	
	@Override
	public boolean wolftail_duringSending() {
		return this.sending;
	}
	
	@Override
	public IdentityHashMap<Consumer<ContentDiff>, H6> wolftail_wrappers() {
		return this.wrappers;
	}
}
