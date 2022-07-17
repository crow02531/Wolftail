package net.wolftail.impl.mixin;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Random;
import java.util.function.Consumer;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.wolftail.impl.ExtensionsMinecraftServer;
import net.wolftail.impl.ImplMPCR;
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
	
	@Final
	@Shadow
	private Random random;
	
	@Unique
	protected ImplMPCR root;
	
	@Unique
	private ContentTracker tracker;
	
	@Unique
	private IdentityHashMap<Consumer<ContentDiff>, H6> wrappers = new IdentityHashMap<>();
	
	@Unique
	private boolean sending;
	
	@Inject(method = "tick", at = @At(value = "FIELD", target = "tickCounter:I", opcode = Opcodes.PUTFIELD))
	private void onTick(CallbackInfo info) {
		this.sending = true;
		
		for(WorldServer w : this.worlds)
			SharedImpls.as(w).wolftail_postTick(this.tickCounter);
		
		this.wrappers.values().forEach(H6::flush);
		
		this.sending = false;
	}
	
	@Inject(method = "stopServer", at = @At(value = "FIELD", target = "playerList:Lnet/minecraft/server/management/PlayerList;", opcode = Opcodes.GETFIELD, ordinal = 0))
	private void onStopServer(CallbackInfo info) throws FileNotFoundException, IOException {
		this.root.onServerStopping();
	}
	
	@Override
	public ImplMPCR wolftail_getRootManager() {
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
	
	@Override
	public Random wolftail_random() {
		return this.random;
	}
}
