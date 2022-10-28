package net.wolftail.internal.tracker.mixin;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.Chunk.EnumCreateEntityType;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.wolftail.internal.tracker.ExtTrackerChunk;
import net.wolftail.internal.tracker.ExtTrackerWorldServer;
import net.wolftail.internal.tracker.container.TimedTrackComplex;
import net.wolftail.internal.tracker.container.TrackContainer;
import net.wolftail.internal.util.collect.LinkedObjectCollection;
import net.wolftail.internal.util.collect.SmallShortSet;
import net.wolftail.util.MoreBlockPos;
import net.wolftail.util.MoreByteBufs;
import net.wolftail.util.tracker.DiffVisitor;
import net.wolftail.util.tracker.Timing;

//anchor of CBS and BTE
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

	@Shadow
	public abstract TileEntity getTileEntity(BlockPos pos, EnumCreateEntityType t);

	@Unique
	private LinkedObjectCollection<Chunk>.Node node;

	@Unique
	private TimedTrackComplex<SmallShortSet> cbs;

	@Unique
	private Map<Timing, Short2ObjectMap<TrackContainer<Void>>> bte;

	@Override
	public boolean wolftail_preventUnload() {
		return this.node != null;
	}

	@Unique
	private void joinIfNecessary() {
		if (this.node == null)
			this.node = ((ExtTrackerWorldServer) this.world).wolftail_join((Chunk) (Object) this);
	}

	@Unique
	private void leaveIfNecessary() {
		if (this.cbs == null && this.bte == null) {
			this.node.unlink();
			this.node = null;
		}
	}

	@Override
	public void wolftail_blockChanged(short index) {
		if (this.cbs != null) {
			this.cbs.forEach(r -> {
				if (r.getMultiB() == null)
					return;

				SmallShortSet set = r.attachment;

				if (set.add(index) && set.isFull()) {
					r.transferB2A();
					r.resetAttachment();
				}
			});
		}
	}

	@Override
	public void wolftail_tileEntityChanged(short index) {
		if (this.bte != null) {
			this.bte.values().forEach(m -> {
				TrackContainer<Void> tc = m.get(index);

				if (tc != null)
					tc.transferB2A();
			});
		}
	}

	@Override
	public boolean wolftail_cbs_track(DiffVisitor acceptor, Timing timing) {
		if (this.cbs == null) {
			this.cbs = new TimedTrackComplex<>(1, () -> new SmallShortSet(64));
			this.cbs.add(timing, acceptor);

			this.joinIfNecessary();

			return true;
		}

		return this.cbs.add(timing, acceptor);
	}

	@Override
	public boolean wolftail_cbs_untrack(DiffVisitor acceptor) {
		if (this.cbs == null)
			return false;

		if (this.cbs.remove(acceptor)) {
			if (this.cbs.isEmpty()) {
				this.cbs = null;

				this.leaveIfNecessary();
			}

			return true;
		}

		return false;
	}

	@Override
	public boolean wolftail_bte_track(short index, DiffVisitor acceptor, Timing timing) {
		Short2ObjectMap<TrackContainer<Void>> target_map;
		TrackContainer<Void> target_tc;

		if (this.bte == null) {
			this.bte = new Object2ObjectArrayMap<>(1);
			this.bte.put(timing, target_map = new Short2ObjectOpenHashMap<>());

			this.joinIfNecessary();
		} else
			target_map = this.bte.get(timing);

		if ((target_tc = target_map.get(index)) == null)
			target_map.put(index, target_tc = new TrackContainer<>());

		return target_tc.add(acceptor);
	}

	@Override
	public boolean wolftail_bte_untrack(short index, DiffVisitor acceptor) {
		if (this.bte == null)
			return false;

		Iterator<Entry<Timing, Short2ObjectMap<TrackContainer<Void>>>> iter = this.bte.entrySet().iterator();

		while (iter.hasNext()) {
			Short2ObjectMap<TrackContainer<Void>> map = iter.next().getValue();
			TrackContainer<Void> tc = map.get(index);

			if (tc != null && tc.remove(acceptor)) {
				if (tc.isEmpty()) {
					map.remove(index);

					if (map.isEmpty()) {
						iter.remove();

						if (this.bte.isEmpty()) {
							this.bte = null;

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
	public void wolftail_cbs_assemble(int tick) {
		if (this.cbs == null)
			return;

		this.cbs.forEach(tick, r -> {
			DiffVisitor v;

			if ((v = r.getMultiA()) != null) {
				v.jzBegin();
				v.jzBindWorld(this.world.provider.getDimensionType());
				v.jzBindChunk(this.x, this.z);

				PacketBuffer buf = new PacketBuffer(Unpooled.buffer());

				for (int i = 0; i < 16; ++i) {
					if (this.storageArrays[i] == null)
						v.jzSetSection(i, null);
					else {
						this.storageArrays[i].getData().write(buf);

						v.jzSetSection(i, buf);
						buf.clear();
					}
				}

				v.jzEnd();
			}

			if ((v = r.getMultiB()) != null) {
				SmallShortSet set = r.attachment;

				if (!set.isEmpty()) {
					v.jzBegin();
					v.jzBindWorld(this.world.provider.getDimensionType());
					v.jzBindChunk(this.x, this.z);

					for (int i = set.size(); i-- != 0;) {
						short s = set.get(i);

						v.jzBindBlock(s);
						v.jzSetState(this.getBlockState(s >> 12 & 15, s & 255, s >> 8 & 15));
					}

					set.clear();

					v.jzEnd();
				}
			}

			r.transferA2B();
		});
	}

	@Override
	public void wolftail_bte_assemble(int tick) {
		if (this.bte == null)
			return;

		MutableBlockPos p = new MutableBlockPos();

		this.bte.forEach((k, map) -> {
			if (k.match(tick)) {
				map.short2ObjectEntrySet().forEach(e -> {
					DiffVisitor v = e.getValue().getMultiA();

					if (v != null) {
						v.jzBegin();
						v.jzBindWorld(this.world.provider.getDimensionType());
						v.jzBindChunk(this.x, this.z);
						v.jzBindBlock(e.getShortKey());
						v.jzSetTileEntity(this.make_tileentity(MoreBlockPos.toPos(this.x, this.z, e.getShortKey(), p)));
						v.jzEnd();

						e.getValue().transferA2B();
					}
				});
			}
		});
	}

	@Unique
	private ByteBuf make_tileentity(BlockPos p) {
		TileEntity t = this.getTileEntity(p, EnumCreateEntityType.CHECK);

		return t == null ? null : MoreByteBufs.writeTag(t.serializeNBT(), Unpooled.buffer());
	}
}
