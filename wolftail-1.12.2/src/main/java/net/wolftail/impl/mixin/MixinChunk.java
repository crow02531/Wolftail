package net.wolftail.impl.mixin;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.wolftail.impl.ExtensionsChunk;
import net.wolftail.impl.ExtensionsWorldServer;
import net.wolftail.impl.ImplCD;
import net.wolftail.impl.SharedImpls;
import net.wolftail.impl.SharedImpls.H3;
import net.wolftail.util.tracker.ContentDiff;
import net.wolftail.util.tracker.SubscribeOrder;

//ContentTracker Supporter
@Mixin(Chunk.class)
public abstract class MixinChunk implements ExtensionsChunk {
	
	@Unique
	private Set<H3> subscribers;
	
	@Unique
	private ExtensionsChunk prev, next;
	
	@Final
	@Shadow
	private World world;
	
	@Final
	@Shadow
	public int x, z;
	
	@Override
	public boolean wolftail_hasSubscriber() {
		return this.subscribers != null;
	}
	
	@Override
	public ExtensionsChunk wolftail_getNext() {
		return this.next;
	}
	
	@Override
	public void wolftail_setNext(ExtensionsChunk c) {
		this.next = c;
	}
	
	@Override
	public void wolftail_register(Consumer<ContentDiff> subscriber) {
		if(this.subscribers == null) {
			this.subscribers = new HashSet<>(8);
			
			ExtensionsWorldServer ews = ((ExtensionsWorldServer) SharedImpls.as(this.world));
			ExtensionsChunk prevHead = ews.wolftail_getHead();
			
			ews.wolftail_setHead(SharedImpls.as(this));
			this.next = prevHead;
		}
		
		if(!this.subscribers.add(new H3(subscriber))) {
			throw new IllegalArgumentException();
		}
	}
	
	@Override
	public void wolftail_tick() {
		SubscribeOrder order = new SubscribeOrder(this.world.provider.getDimensionType(), this.x, this.z);
		
		for(H3 e : this.subscribers) {
			if(e.initial) {
				ImplCD sent = new ImplCD(order);
				
				//TODO make initial data
				
				e.subscriber.accept(sent);
				e.initial = false;
			} else {
				//TODO send diff
			}
		}
	}
	
	@Override
	public void wolftail_unregister(Consumer<ContentDiff> subscriber) {
		this.subscribers.remove(new H3(subscriber));
		
		if(this.subscribers.isEmpty()) {
			((ChunkProviderServer) this.world.getChunkProvider()).queueUnload(SharedImpls.as(this));
			
			if(this.prev == null) {
				((ExtensionsWorldServer) SharedImpls.as(this.world)).wolftail_setHead(this.next);
			} else {
				this.prev.wolftail_setNext(this.next);
			}
			
			this.subscribers = null;
		}
	}
}
