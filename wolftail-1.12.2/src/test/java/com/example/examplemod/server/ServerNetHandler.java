package com.example.examplemod.server;

import com.example.examplemod.network.S2CContentDiff;

import net.minecraft.entity.passive.EntityPig;
import net.minecraft.network.INetHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.wolftail.api.ServerPlayContext;
import net.wolftail.util.tracker.ContentTracker;
import net.wolftail.util.tracker.ContentOrder;

public class ServerNetHandler implements INetHandler, ITickable {
	
	private final ServerPlayContext context;
	
	private EntityPig playEntity;
	
	ServerNetHandler(ServerPlayContext context) {
		MinecraftServer server = (this.context = context).manager().rootManager().server();
		WorldServer w = server.worlds[0];
		EntityPig p = this.playEntity = new EntityPig(w);
		
		BlockPos sp = w.getSpawnPoint();
		
		p.setLocationAndAngles(sp.getX(), sp.getY(), sp.getZ(), 0, 0);
		p.setCustomNameTag(context.playName());
		p.setAlwaysRenderNameTag(true);
		w.spawnEntity(p);
		
		ContentTracker.instanceFor(server).subscribe(new ContentOrder(DimensionType.OVERWORLD, 0, 0), (d) -> {
			this.context.sendPacket(new S2CContentDiff(d));
		});
	}
	
	public EntityPig getPlayEntity() {
		return this.playEntity;
	}
	
	@Override
	public void update() {
		EntityPig p = this.playEntity;
		
		if(p.isDead) {
			this.context.disconnect();
		} else {
			p.fallDistance = 0;
		}
	}
	
	@Override
	public void onDisconnect(ITextComponent reason) {
		this.playEntity.setDead();
	}
}
