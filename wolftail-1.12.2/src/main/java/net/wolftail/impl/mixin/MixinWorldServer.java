package net.wolftail.impl.mixin;

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
import net.wolftail.impl.ServerWorldListener;
import net.wolftail.impl.SharedImpls;

//ContentTracker Supporter, ticking subscribed chunks and adding an IWorldEventListener
@Mixin(WorldServer.class)
public abstract class MixinWorldServer implements ExtensionsWorldServer {
	
	@Unique
	private ExtensionsChunk head;
	
	@Inject(method = "tick", at = @At(value = "INVOKE_STRING", target = "endStartSection(Ljava/lang/String;)V", args = "ldc=chunkMap", shift = Shift.AFTER))
	private void onTick(CallbackInfo info) {
		ExtensionsChunk c = this.head;
		
		while(c != null) {
			c.wolftail_tick();
			
			c = c.wolftail_getNext();
		}
	}
	
	@Inject(method = "init", at = @At("RETURN"))
	private void onInit(CallbackInfoReturnable<World> info) {
		((WorldServer) SharedImpls.as(this)).addEventListener(new ServerWorldListener());
	}
	
	@Override
	public ExtensionsChunk wolftail_getHead() {
		return this.head;
	}
	
	@Override
	public void wolftail_setHead(ExtensionsChunk h) {
		this.head = h;
	}
}
