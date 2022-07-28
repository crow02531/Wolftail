package net.wolftail.impl.tracker.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import io.netty.buffer.Unpooled;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.wolftail.impl.tracker.ExtTrackerChunk;
import net.wolftail.impl.tracker.ExtTrackerWorldServer;
import net.wolftail.impl.tracker.TrackContainer;
import net.wolftail.impl.util.collect.LinkedObjectCollection;
import net.wolftail.impl.util.collect.SmallShortSet;
import net.wolftail.util.tracker.DiffVisitor;
import net.wolftail.util.tracker.Timing;

//anchor of CBS
@Mixin(Chunk.class)
public abstract class MixinChunk implements ExtTrackerChunk {
	
	@Final
	@Shadow
	public ExtendedBlockStorage[] storageArrays;
	
	@Final
	@Shadow
	public World world;
	
	@Final
	@Shadow
	public int x, z;
	
	@Shadow
	public abstract IBlockState getBlockState(int x, int y, int z);
	
	@Unique
	private LinkedObjectCollection<Chunk>.Node node;
	
	@Unique
	private TrackContainer<SmallShortSet> cbs;
	
	@Override
	public boolean wolftail_preventUnload() {
		return this.node != null;
	}
	
	@Override
	public void wolftail_blockChanged(short index) {
		if(this.cbs != null) {
			this.cbs.forEach(r -> {
				if(r.getMultiB() == null) return;
				
				SmallShortSet set = r.attachment;
				
				if(set.add(index) && set.isFull()) {
					r.transferB2A();
					r.resetAttachment();
				}
			});
		}
	}
	
	@Override
	public void wolftail_tileEntityChanged(short index) {
		
	}
	
	@Override
	public boolean wolftail_cbs_track(DiffVisitor acceptor, Timing timing) {
		if(this.cbs == null) {
			this.cbs = new TrackContainer<>(1, () -> new SmallShortSet(64));
			this.node = ((ExtTrackerWorldServer) this.world).wolftail_join((Chunk) (Object) this);
			
			this.cbs.add(timing, acceptor);
			
			return true;
		}
		
		return this.cbs.add(timing, acceptor);
	}
	
	@Override
	public boolean wolftail_cbs_untrack(DiffVisitor acceptor) {
		if(this.cbs == null) return false;
		
		if(this.cbs.remove(acceptor)) {
			if(this.cbs.isEmpty()) {
				this.cbs = null;
				
				this.node.unlink();
				this.node = null;
			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void wolftail_assemble(int tick) {
		if(this.cbs == null) return;
		
		this.cbs.forEach(tick, r -> {
			DiffVisitor v;
			
			if((v = r.getMultiA()) != null) {
				v.jzBegin();
				v.jzBindWorld(this.world.provider.getDimensionType());
				v.jzBindChunk(this.x, this.z);
				
				PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
				
				for(int i = 0; i < 16; ++i) {
					if(this.storageArrays[i] == null) v.jzSetSection(i, null);
					else {
						this.storageArrays[i].getData().write(buf);
						
						v.jzSetSection(i, buf);
						buf.readerIndex(0).writerIndex(0);
					}
				}
				
				v.jzEnd();
			}
			
			if((v = r.getMultiB()) != null) {
				v.jzBegin();
				v.jzBindWorld(this.world.provider.getDimensionType());
				v.jzBindChunk(this.x, this.z);
				
				SmallShortSet set = r.attachment;
				
				for(int i = set.size(); i-- != 0;) {
					short s = set.get(i);
					
					v.jzBindBlock(s);
					v.jzSetState(this.getBlockState(s >> 12 & 15, s & 255, s >> 8 & 15));
				}
				
				v.jzEnd();
			}
			
			r.transferA2B();
		});
	}
}
