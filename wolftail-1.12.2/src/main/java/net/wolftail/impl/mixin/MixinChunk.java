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
import net.wolftail.impl.SEntry;
import net.wolftail.impl.SharedImpls;
import net.wolftail.util.tracker.ContentDiff;
import net.wolftail.util.tracker.SubscribeOrder;

//ContentTracker Supporter
@Mixin(Chunk.class)
public abstract class MixinChunk implements ExtensionsChunk {
	
	@Unique
	private Set<SEntry> subscribers;
	
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
		return this.subscribers != null && !this.subscribers.isEmpty();
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
		
		if(!this.subscribers.add(new SEntry(subscriber))) {
			throw new IllegalArgumentException();
		}
	}
	
	@Override
	public void wolftail_tick() {
		SubscribeOrder order = new SubscribeOrder(this.world.provider.getDimensionType(), this.x, this.z);
		
		for(SEntry e : this.subscribers) {
			if(e.initial) {
				ContentDiff sent = SharedImpls.H2.content_diff_factory.apply(order);
				
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
		this.subscribers.remove(new SEntry(subscriber));
		
		if(this.subscribers.isEmpty()) {
			((ChunkProviderServer) this.world.getChunkProvider()).queueUnload(SharedImpls.as(this));
			
			if(this.prev == null) {
				((ExtensionsWorldServer) SharedImpls.as(this.world)).wolftail_setHead(this.next);
			} else {
				this.prev.wolftail_setNext(this.next);
			}
		}
	}
}
