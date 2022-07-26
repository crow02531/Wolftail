package net.wolftail.impl.core.mixin;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NettyPacketEncoder;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCustomPayload;

//WPS supporter
@Mixin(NettyPacketEncoder.class)
public abstract class MixinNettyPacketEncoder {
	
	@Final
	@Shadow
	public static Logger LOGGER;
	
	@Final
	@Shadow
	public static Marker RECEIVED_PACKET_MARKER;
	
	@Final
	@Shadow
	public EnumPacketDirection direction; //target logic side
	
	@Unique
	private static final Logger logger = LogManager.getLogger("wolftail/network");
	
	@Inject(method = "encode", at = @At(value = "INVOKE", target = "isDebugEnabled()Z"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void onEncode(ChannelHandlerContext ctx, Packet<?> pkt, ByteBuf buf, CallbackInfo info, EnumConnectionState state, Integer pid) throws IOException {
		if(pid == null && state == EnumConnectionState.PLAY) {
			info.cancel();
			
			if(logger.isDebugEnabled())
				logger.debug("=OUT-PRE	: wild packet {}", pkt.getClass().getName());
			
			/*
			 * Wild Packet Support(WPS)
			 * 
			 * 	Our implement is based on CustomPayload mechanism, in the most of the time,
			 * 	A WPS packet looks like this VarInt[24];String[WT|WPS];Byte[0];String[com.exmplemod.SomePacket];Bytes[Its payload]
			 * 
			 * 	The 24 can be 9, they both are CustomPayloadPacket's packet id, the former is for SPacketCustomPayload,
			 * 	the later is for CPacketCustomPayload.
			 * 
			 * 	However in the protocol, CustomPayload has size restriction, S2C's payload can't larger than 1048576 bytes,
			 * 	C2S's is 32767, this restriction was bad for Wild Packets since sometimes you need to send a large Wild Packet.
			 * 
			 * 	So sometimes you will see a WPS packet looks like this
			 * 		24;"WT|WPS";1;"com.exmplemod.SomePacket";Payload_first_part
			 * 	and after receiving this, you soon will get another WPS packet:
			 * 		24;"WT|WPS";0;Payload_second_part
			 * 
			 * 	To support large size Wild Packet, we simply divide your packet into several parts, send them out
			 * 	using the CustomPayloadPacket and when receiving, we join the divided parts up.
			 * 
			 * 	The byte 0 indicates there will be no following-up packet, while 1 means the contrary.
			 * */
			
			final int id = this.direction == EnumPacketDirection.CLIENTBOUND ? 24 : 9;
			final int maxload = this.direction == EnumPacketDirection.CLIENTBOUND ? 1048576 : 32767;
			final String channel = "WT|WPS";
			
			PacketBuffer wrapper = new PacketBuffer(buf);
			
			int i0 = wrapper.writerIndex();
			
			//write CustomPayload header
			wrapper.writeVarInt(id);
			wrapper.writeString(channel);
			
			final int length_headers = wrapper.writerIndex() - i0 + 1; //the 1 is the WPS header is first byte
			
			i0 = wrapper.writerIndex();
			
			//write WPS header
			wrapper.writeByte(0);
			wrapper.writeString(pkt.getClass().getName());
			//write packet content
			pkt.writePacketData(wrapper);
			
			int rawload = wrapper.markWriterIndex().writerIndex() - i0;
			
			if(logger.isDebugEnabled())
				logger.debug("=OUT-POST	: with custom payload size {} bytes", rawload);
			
			logDebug(state, id);
			
			if(rawload > maxload) {
				//outbound the first packet
				buf.setByte(i0, 1).writerIndex(i0 + maxload);
				ctx.write(buf.retainedSlice());
				
				boolean has_following;
				
				do {
					has_following = (rawload -= maxload) > maxload;
					
					writer_backward(buf, length_headers);
					wrapper.writeVarInt(id);
					wrapper.writeString(channel);
					wrapper.writeByte(has_following ? 1 : 0);
					writer_backward(buf, length_headers);
					
					buf.readerIndex(buf.writerIndex());
					
					if(has_following) {
						logDebug(state, id);
						buf.writerIndex(buf.readerIndex() + length_headers + maxload);
						ctx.write(buf.retainedSlice());
					} else {
						logDebug(state, id);
						buf.resetWriterIndex();
						
						break; //the last following packet's writing was assigned to MessageToByteEncoder since there was a ChannelPromise
					}
				} while(true);
			}
		}
	}
	
	@Unique
	private static void writer_backward(ByteBuf buf, int bytes) {
		buf.writerIndex(buf.writerIndex() - bytes);
	}
	
	@Unique
	private static void logDebug(EnumConnectionState state, int id) {
		if(LOGGER.isDebugEnabled()) {
			Class<?> klass = id == 24 ? SPacketCustomPayload.class : CPacketCustomPayload.class;
			
			LOGGER.debug(RECEIVED_PACKET_MARKER, "OUT: [{}:{}] {}", state, id, klass.getName());
		}
	}
}
