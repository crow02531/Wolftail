package net.wolftail.impl.mixin;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import it.unimi.dsi.fastutil.shorts.ShortArraySet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.wolftail.impl.ExtensionsChunk;
import net.wolftail.impl.ExtensionsWorldServer;
import net.wolftail.impl.ImplCD;
import net.wolftail.impl.SharedImpls;
import net.wolftail.impl.SharedImpls.H3;
import net.wolftail.impl.SharedImpls.H4;
import net.wolftail.util.tracker.ContentDiff;
import net.wolftail.util.tracker.ContentType;
import net.wolftail.util.tracker.OrderChunkBlock;

//ContentTracker Supporter
@Mixin(Chunk.class)
public abstract class MixinChunk implements ExtensionsChunk {
	
	@Unique
	private Set<H3> subscribers;
	
	@Unique
	private ExtensionsChunk prev, next;
	
	@Unique
	private ShortSet changedBlocks;
	
	@Final
	@Shadow
	private World world;
	
	@Final
	@Shadow
	public int x, z;
	
	@Override
	public void wolftail_blockChanged(int localX, int localY, int localZ) {
		if(this.subscribers != null)
			this.changedBlocks.add((short) (localX << 12 | localZ << 8 | localY));
	}
	
	@Override
	public ShortSet wolftail_changedBlocks() {
		return this.changedBlocks;
	}
	
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
			this.changedBlocks = new ShortArraySet(32); //how do a short set has its size larger than 65536?
			
			ExtensionsWorldServer ews = ((ExtensionsWorldServer) SharedImpls.as(this.world));
			ExtensionsChunk prevHead = ews.wolftail_getHead();
			
			ews.wolftail_setHead(SharedImpls.as(this));
			this.next = prevHead;
		}
		
		if(!this.subscribers.add(new H3(subscriber)))
			throw new IllegalArgumentException();
	}
	
	@Override
	public void wolftail_tick() {
		OrderChunkBlock order = ContentType.orderBlock(this.world.provider.getDimensionType(), this.x, this.z);
		
		ImplCD init = null;
		ImplCD diff = null;
		
		int changes = this.changedBlocks.size();
		
		for(H3 e : this.subscribers) {
			if(e.initial) {
				if(init == null)
					init = new ImplCD(order, H4.make_CB_Init(order, SharedImpls.as(this)));
				
				e.subscriber.accept(init);
				e.initial = false;
			} else if(changes > 0) {
				if(diff == null) {
					if(changes >= 64) {
						if((diff = init) == null) {
							diff = init = new ImplCD(order, H4.make_CB_Init(order, SharedImpls.as(this)));
						}
					} else diff = new ImplCD(order, H4.make_CB_Diff(order, SharedImpls.as(this)));
					
					this.changedBlocks.clear();
				}
				
				e.subscriber.accept(diff);
			}
		}
	}
	
	@Override
	public void wolftail_unregister(Consumer<ContentDiff> subscriber) {
		if(this.subscribers == null) return;
		
		this.subscribers.remove(new H3(subscriber));
		
		if(this.subscribers.isEmpty()) {
			((ChunkProviderServer) this.world.getChunkProvider()).queueUnload(SharedImpls.as(this));
			
			if(this.prev == null) {
				((ExtensionsWorldServer) SharedImpls.as(this.world)).wolftail_setHead(this.next);
			} else {
				this.prev.wolftail_setNext(this.next);
			}
			
			this.subscribers = null;
			this.changedBlocks = null;
		}
	}
}
