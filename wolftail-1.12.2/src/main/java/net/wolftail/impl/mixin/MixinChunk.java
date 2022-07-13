package net.wolftail.impl.mixin;

import java.util.HashMap;
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
import net.wolftail.impl.SharedImpls.H4;
import net.wolftail.impl.SmallLong2ObjectMap;
import net.wolftail.impl.SmallShortSet;
import net.wolftail.util.tracker.ContentDiff;
import net.wolftail.util.tracker.ContentType;
import net.wolftail.util.tracker.OrderChunkNormal;

//ContentTracker Supporter
@Mixin(Chunk.class)
public abstract class MixinChunk implements ExtensionsChunk {
	
	@Unique
	private static final SmallShortSet DUMMY = new SmallShortSet();
	
	@Unique
	private HashMap<H3, H3> subscribers_CB;
	
	@Unique
	private ExtensionsChunk prev, next;
	
	@Unique
	private SmallLong2ObjectMap<SmallShortSet> changedBlocks;
	
	@Final
	@Shadow
	private World world;
	
	@Final
	@Shadow
	public int x, z;
	
	@Override
	public void wolftail_blockChanged(int localX, int localY, int localZ) {
		if(this.subscribers_CB != null) {
			SmallLong2ObjectMap<SmallShortSet> cbs = this.changedBlocks;
			
			for(int i = cbs.size(); i-- != 0;) {
				SmallShortSet set = cbs.getVal(i);
				
				if(set != DUMMY && set.add((short) (localX << 12 | localZ << 8 | localY))) {
					if(set.isFull()) cbs.setVal(i, DUMMY);
				}
			}
		}
	}
	
	@Override
	public boolean wolftail_hasSubscriber() {
		return this.subscribers_CB != null;
	}
	
	@Override
	public ExtensionsChunk wolftail_getNext() {
		return this.next;
	}
	
	@Override
	public void wolftail_setNext(ExtensionsChunk c) {
		this.next = c;
	}
	
	@Unique
	private void join_chain() {
		ExtensionsWorldServer ews = ((ExtensionsWorldServer) SharedImpls.as(this.world));
		ExtensionsChunk prevHead = ews.wolftail_getHead();
		
		ews.wolftail_setHead(SharedImpls.as(this));
		this.next = prevHead;
	}
	
	@Unique
	private void leave_chain() {
		if(this.prev == null) {
			((ExtensionsWorldServer) SharedImpls.as(this.world)).wolftail_setHead(this.next);
		} else {
			this.prev.wolftail_setNext(this.next);
		}
	}
	
	@Override
	public void wolftail_register_CB(H3 subscribeEntry) {
		if(this.subscribers_CB != null) {
			if(this.subscribers_CB.putIfAbsent(subscribeEntry, subscribeEntry) != null)
				throw new IllegalArgumentException();
			
			if(!this.changedBlocks.containsKey(subscribeEntry.tickSequence))
				this.changedBlocks.put(subscribeEntry.tickSequence, new SmallShortSet(64));
		} else {
			if(!this.wolftail_hasSubscriber())
				this.join_chain();
			
			(this.subscribers_CB = new HashMap<>(8)).put(subscribeEntry, subscribeEntry);
			this.changedBlocks = new SmallLong2ObjectMap<>(subscribeEntry.tickSequence, new SmallShortSet(64));
		}
	}
	
	@Override
	public void wolftail_unregister_CB(Consumer<ContentDiff> subscriber) {
		if(this.subscribers_CB == null) return;
		
		H3 entry = this.subscribers_CB.remove(new H3(subscriber));
		
		if(entry == null) return;
		
		if(this.subscribers_CB.isEmpty()) {
			this.subscribers_CB = null;
			this.changedBlocks = null;
			
			if(!this.wolftail_hasSubscriber()) {
				((ChunkProviderServer) this.world.getChunkProvider()).queueUnload(SharedImpls.as(this));
				
				this.leave_chain();
			}
		} else {
			for(H3 e : this.subscribers_CB.keySet()) {
				if(e.tickSequence == entry.tickSequence)
					return;
			}
			
			this.changedBlocks.remove(entry.tickSequence);
		}
	}
	
	@Override
	public void wolftail_postTick(int tick) {
		this.postTick_CB(tick);
	}
	
	@Unique
	private void postTick_CB(int tick) {
		OrderChunkNormal order = ContentType.orderBlock(this.world.provider.getDimensionType(), this.x, this.z);
		
		ImplCD init = null;
		SmallLong2ObjectMap<ImplCD> diffs = new SmallLong2ObjectMap<>(this.changedBlocks.size());
		
		for(H3 e : this.subscribers_CB.keySet()) {
			if(e.initial) {
				if(init == null)
					init = new ImplCD(order, H4.make_CB_init(order, SharedImpls.as(this)));
				
				e.subscriber.accept(init);
				e.initial = false;
			} else if(e.shouldSend(tick)) {
				ImplCD diff = diffs.get(e.tickSequence);
				
				if(diff == null) {
					SmallShortSet changes = this.changedBlocks.get(e.tickSequence);
					
					if(changes == DUMMY) {
						if((diff = init) == null)
							diff = init = new ImplCD(order, H4.make_CB_init(order, SharedImpls.as(this)));
						
						this.changedBlocks.put(e.tickSequence, new SmallShortSet(64));
					} else if(changes.size() > 0) {
						diff = new ImplCD(order, H4.make_CB_diff(order, SharedImpls.as(this), changes));
						
						changes.clear();
					}
					
					if(diff != null)
						diffs.put(e.tickSequence, diff);
				}
				
				if(diff != null)
					e.subscriber.accept(diff);
			}
		}
	}
}
