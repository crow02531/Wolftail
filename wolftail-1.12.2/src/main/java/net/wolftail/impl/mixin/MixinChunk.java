package net.wolftail.impl.mixin;

import java.util.HashMap;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.Chunk.EnumCreateEntityType;
import net.wolftail.impl.ExtensionsChunk;
import net.wolftail.impl.SharedImpls;
import net.wolftail.impl.SharedImpls.H2;
import net.wolftail.impl.SharedImpls.H3;
import net.wolftail.impl.SharedImpls.H4;
import net.wolftail.impl.SharedImpls.H6;
import net.wolftail.impl.SharedImpls.H7;
import net.wolftail.impl.util.collect.LinkedObjectCollection;
import net.wolftail.impl.util.collect.SmallLong2ObjectMap;
import net.wolftail.impl.util.collect.SmallShortSet;
import net.wolftail.util.tracker.ContentType;
import net.wolftail.util.tracker.OrderBlockNormal;
import net.wolftail.util.tracker.OrderChunkNormal;

//ContentTracker Supporter
@Mixin(Chunk.class)
public abstract class MixinChunk implements ExtensionsChunk {
	
	@Unique
	private static final SmallShortSet DUMMY = new SmallShortSet();
	
	@Unique
	private HashMap<H3, H3> subscribers_CB;
	
	@Unique
	private SmallLong2ObjectMap<SmallShortSet> changedBlocks;
	
	@Unique
	private SmallLong2ObjectMap<Short2ObjectOpenHashMap<H7>> subscribers_BTE;
	
	@Unique
	private LinkedObjectCollection<Chunk>.Node node;
	
	@Final
	@Shadow
	private World world;
	
	@Final
	@Shadow
	public int x, z;
	
	@Shadow
	public abstract TileEntity getTileEntity(BlockPos pos, EnumCreateEntityType p_177424_2_);
	
	@Override
	public boolean wolftail_hasSubscriber() {
		return this.subscribers_CB != null || this.subscribers_BTE != null;
	}
	
	@Unique
	private void joinIfNecessary() {
		if(!this.wolftail_hasSubscriber())
			this.node = SharedImpls.as((WorldServer) this.world).wolftail_join(SharedImpls.as(this));
	}
	
	@Unique
	private void leaveIfNecessary() {
		if(!this.wolftail_hasSubscriber()) {
			WorldServer w = (WorldServer) this.world;
			
			if(!w.getPlayerChunkMap().contains(this.x, this.z))
				w.getChunkProvider().queueUnload(SharedImpls.as(this));
			
			this.node.unlink();
			this.node = null;
		}
	}
	
	@Override
	public void wolftail_register_CB(H3 subscribeEntry) {
		if(this.subscribers_CB != null) {
			if(this.subscribers_CB.putIfAbsent(subscribeEntry, subscribeEntry) != null)
				throw new IllegalArgumentException();
			
			if(!this.changedBlocks.containsKey(subscribeEntry.tickSequence))
				this.changedBlocks.put(subscribeEntry.tickSequence, new SmallShortSet(H4.THRESHOLD_ABANDON));
		} else {
			this.joinIfNecessary();
			
			(this.subscribers_CB = new HashMap<>(8)).put(subscribeEntry, subscribeEntry);
			this.changedBlocks = new SmallLong2ObjectMap<>(subscribeEntry.tickSequence, new SmallShortSet(H4.THRESHOLD_ABANDON));
		}
	}
	
	@Override
	public boolean wolftail_unregister_CB(H6 wrapper) {
		if(this.subscribers_CB == null) return false;
		
		H3 entry = this.subscribers_CB.remove(new H3(wrapper));
		if(entry == null) return false;
		
		if(this.subscribers_CB.isEmpty()) {
			this.subscribers_CB = null;
			this.changedBlocks = null;
			
			this.leaveIfNecessary();
		} else {
			for(H3 e : this.subscribers_CB.keySet()) {
				if(e.tickSequence == entry.tickSequence)
					return true;
			}
			
			this.changedBlocks.remove(entry.tickSequence);
		}
		
		return true;
	}
	
	@Override
	public void wolftail_register_BTE(H3 subscribeEntry, short index) {
		Short2ObjectOpenHashMap<H7> m = null;
		H7 l = null;
		
		if(this.subscribers_BTE != null) {
			this.checkExistInAllFrequency(subscribeEntry, index);
			
			if((m = this.subscribers_BTE.get(subscribeEntry.tickSequence)) != null)
				l = m.get(index);
		} else {
			this.joinIfNecessary();
			
			this.subscribers_BTE = new SmallLong2ObjectMap<>();
		}
		
		if(m == null)
			this.subscribers_BTE.put(subscribeEntry.tickSequence, m = new Short2ObjectOpenHashMap<>());
		if(l == null)
			m.put(index, l = new H7());
		
		l.add(subscribeEntry);
		l.mark_initial = true;
	}
	
	@Unique
	private void checkExistInAllFrequency(H3 e, short index) {
		SmallLong2ObjectMap<Short2ObjectOpenHashMap<H7>> ct = this.subscribers_BTE;
		
		for(int i = ct.size(); i-- != 0;) {
			H7 l = ct.getVal(i).get(index);
			
			if(l != null && l.contains(e))
				throw new IllegalArgumentException();
		}
	}
	
	@Override
	public boolean wolftail_unregister_BTE(H6 wrapper, short index) {
		SmallLong2ObjectMap<Short2ObjectOpenHashMap<H7>> ct = this.subscribers_BTE;
		if(ct == null) return false;
		
		H3 e = new H3(wrapper);
		
		for(int i = ct.size(); i-- != 0;) {
			Short2ObjectOpenHashMap<H7> m = ct.getVal(i);
			H7 l = m.get(index);
			
			if(l != null && l.remove(e)) {
				if(l.isEmpty()) {
					m.remove(index);
					
					if(m.isEmpty()) {
						ct.rem(i);
						
						if(ct.isEmpty()) {
							this.subscribers_BTE = null;
							
							this.leaveIfNecessary();
						}
					}
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void wolftail_blockChanged(short index) {
		if(this.subscribers_CB != null) {
			SmallLong2ObjectMap<SmallShortSet> cbs = this.changedBlocks;
			
			for(int i = cbs.size(); i-- != 0;) {
				SmallShortSet set = cbs.getVal(i);
				
				if(set != DUMMY && set.add(index) && set.isFull())
					cbs.setVal(i, DUMMY);
			}
		}
	}
	
	@Override
	public void wolftail_tileEntityChanged(short index) {
		if(this.subscribers_BTE != null) {
			SmallLong2ObjectMap<Short2ObjectOpenHashMap<H7>> ct = this.subscribers_BTE;
			
			for(int i = ct.size(); i-- != 0;) {
				H7 l = ct.getVal(i).get(index);
				
				if(l != null) l.mark_changed = true;
			}
		}
	}
	
	@Override
	public void wolftail_postTick(int tick) {
		if(this.subscribers_CB != null) this.postTick_CB(tick);
		if(this.subscribers_BTE != null) this.postTick_BTE(tick);
	}
	
	@Unique
	private void postTick_BTE(int tick) {
		SmallLong2ObjectMap<Short2ObjectOpenHashMap<H7>> ct = this.subscribers_BTE;
		
		for(int i = ct.size(); i-- != 0;) {
			if(H3.match(ct.getKey(i), tick)) {
				Short2ObjectOpenHashMap<H7> m = ct.getVal(i);
				
				for(Short2ObjectMap.Entry<H7> e : m.short2ObjectEntrySet()) {
					H7 l = e.getValue();
					
					if(!l.mark_changed && !l.mark_initial) continue;
					
					OrderBlockNormal order = ContentType.orderTileEntity(this.world.provider.getDimensionType(), H2.toPos(this.x, this.z, e.getShortKey()));
					ByteBuf sent = H4.make_BTE(order, this.getTileEntity(order.position(), EnumCreateEntityType.CHECK));
					
					for(H3 se : l) {
						if(se.initial) {
							se.initial = false;
							
							se.wrapper.cumulate(order, sent);
						} else if(l.mark_changed) {
							se.wrapper.cumulate(order, sent);
						}
					}
					
					l.mark_initial = false;
					l.mark_changed = false;
				}
			}
		}
	}
	
	@Unique
	private void postTick_CB(int tick) {
		OrderChunkNormal order = ContentType.orderBlock(this.world.provider.getDimensionType(), this.x, this.z);
		
		ByteBuf init = null;
		SmallLong2ObjectMap<ByteBuf> diffs = new SmallLong2ObjectMap<>(this.changedBlocks.size());
		
		for(H3 e : this.subscribers_CB.keySet()) {
			if(e.initial) {
				if(init == null)
					init = H4.make_CB_init(order, SharedImpls.as(this));
				
				e.wrapper.cumulate(order, init);
				e.initial = false;
			} else if(e.shouldSend(tick)) {
				ByteBuf diff = diffs.get(e.tickSequence);
				
				if(diff == null) {
					SmallShortSet changes = this.changedBlocks.get(e.tickSequence);
					
					if(changes == DUMMY) {
						if((diff = init) == null)
							diff = init = H4.make_CB_init(order, SharedImpls.as(this));
						
						this.changedBlocks.put(e.tickSequence, new SmallShortSet(H4.THRESHOLD_ABANDON));
					} else if(changes.size() > 0) {
						diff = H4.make_CB_diff(order, SharedImpls.as(this), changes);
						
						changes.clear();
					}
					
					if(diff != null)
						diffs.put(e.tickSequence, diff);
				}
				
				if(diff != null)
					e.wrapper.cumulate(order, diff);
			}
		}
	}
}
