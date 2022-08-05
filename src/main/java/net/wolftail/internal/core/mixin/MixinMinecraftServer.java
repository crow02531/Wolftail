package net.wolftail.internal.core.mixin;

import java.io.IOException;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.wolftail.internal.core.ExtCoreMinecraftServer;
import net.wolftail.internal.core.ImplMPCR;
import net.wolftail.internal.core.ImplPCS;

//root manager: add root manager, loadDat, saveDat
//statusResponse
@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer implements ExtCoreMinecraftServer {
	
	@Unique
	protected ImplMPCR root;
	
	@Inject(method = "saveAllWorlds", at = @At("HEAD"))
	private void on_saveAllWorlds_head(CallbackInfo ci) throws IOException {
		this.root.saveDat();
	}
	
	@Inject(method = "initialWorldChunkLoad", at = @At("HEAD"))
	private void on_initialWorldChunkLoad_head(CallbackInfo ci) throws IOException {
		this.root.loadDat();
	}
	
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "net.minecraft.server.MinecraftServer.getCurrentPlayerCount()I"))
	private int proxy_tick_getCurrentPlayerCount(MinecraftServer s) {
		return this.root.currentLoad();
	}
	
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "java.util.List.get(I)Ljava/lang/Object;", remap = false))
	private Object proxy_tick_get(List<EntityPlayerMP> l, int i) {
		this.tmpIndex = i;
		
		return null;
	}
	
	@Unique
	private int tmpIndex;
	
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "net.minecraft.entity.player.EntityPlayerMP.getGameProfile()Lcom/mojang/authlib/GameProfile;"))
	private GameProfile proxy_tick_getGameProfile(EntityPlayerMP p) {
		ImplPCS pc = this.root.contextAt(this.tmpIndex);
		
		return new GameProfile(pc.playId(), pc.playName());
	}
	
	@Override
	public ImplMPCR wolftail_getRootManager() {
		return this.root;
	}
}
