package net.wolftail.impl.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public final class Constants {
	
	private Constants() {}
	
	public static final ResourceLocation CHANNEL_LOGIN_TYPE_NOTIFY = new ResourceLocation("wolftail", "type_notify");
	public static final ResourceLocation CHANNEL_PLAY_PAYLOAD = new ResourceLocation("wolftail", "payload");
	
	public static FriendlyByteBuf newOrReturn(ByteBuf buf) {
		return buf instanceof FriendlyByteBuf ? (FriendlyByteBuf) buf : new FriendlyByteBuf(buf);
	}
}
