package net.wolftail.impl.mixin;

import java.io.IOException;
import java.util.List;

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
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NettyPacketDecoder;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.wolftail.impl.SharedImpls;

//WPS supporter
@Mixin(NettyPacketDecoder.class)
public abstract class MixinNettyPacketDecoder {
	
	@Final
	@Shadow
	private static Logger LOGGER;
	
	@Final
	@Shadow
	private static Marker RECEIVED_PACKET_MARKER;
	
	@Final
	@Shadow
	private EnumPacketDirection direction; //current logic side
	
	@Unique
	private Packet<?> wild_packet;
	
	@Unique
	private CompositeByteBuf cumulation;
	
	@Inject(method = "decode", at = @At(value = "INVOKE", target = "getPacket(Lnet/minecraft/network/EnumPacketDirection;I)Lnet/minecraft/network/Packet;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void onDecode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> list, CallbackInfo info, PacketBuffer wrapper, int id) throws IOException {
		EnumConnectionState state = ctx.channel().attr(NetworkManager.PROTOCOL_ATTRIBUTE_KEY).get();
		
		boolean handledWPS = false;
		
		if(state == EnumConnectionState.PLAY && id == SharedImpls.H2.custom_payload_pid(this.direction)) {
			wrapper.markReaderIndex();
			
			if(wrapper.readString(20).equals("WOLFTAIL|WPS")) {
				info.cancel();
				handledWPS = true;
				
				logDebug(state, id);
				this.onWPSChannel(wrapper, list);
			} else wrapper.resetReaderIndex();
		}
		
		if(!handledWPS && this.wild_packet != null)
			throw new IOException("Unexpected packet " + state + ":" + id + " , expecting WPS packet");
	}
	
	@Unique
	private void onWPSChannel(PacketBuffer wrapper, List<Object> list) throws IOException {
		if(wrapper.readableBytes() > SharedImpls.H2.custom_payload_maxload(this.direction))
			throw new IOException("Payload may not be larger than " + SharedImpls.H2.custom_payload_maxload(this.direction) + " bytes");
		
		//read WPS header
		byte op = wrapper.readByte();
		String packet_name = this.wild_packet == null ? wrapper.readString(32767) : null;
		
		//having a packet_name means a new start, otherwise means we already have a wild_packet and a wild_payload
		if(packet_name != null) {
			if(SharedImpls.LOGGER_NETWORK.isDebugEnabled())
				SharedImpls.LOGGER_NETWORK.debug("=IN-PRE	: try starting with wild packet {}", packet_name);
			
			try {
				this.wild_packet = (Packet<?>) Class.forName(packet_name).newInstance();
			} catch(InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				throw new IOException("Bad wild packet ".concat(packet_name), e);
			}
			
			if(op != 0) this.cumulation = Unpooled.compositeBuffer(Integer.MAX_VALUE);
			
			//we now have started, with a newly created wild_packet, and a wild_payload if we have following-up packet
		}
		
		CompositeByteBuf cumulation = this.cumulation; //maybe null
		Packet<?> packet = this.wild_packet; //never be null
		
		//'wrapper' was obtained by readBytes(I), see NettyVarint21FrameDecoder
		
		if(cumulation == null) {
			//a null wild_payload here means we are in a new-start and have no following packets
			
			try {
				packet.readPacketData(wrapper);
				checkRemaining(wrapper, packet_name);
				
				list.add(packet);
				
				if(SharedImpls.LOGGER_NETWORK.isDebugEnabled())
					SharedImpls.LOGGER_NETWORK.debug("=IN-POST	: with 1 segment packets");
			} finally {
				this.wild_packet = null;
			}
		} else {
			//this means we may in a new-start with followings, or just handling a following packet
			
			//cumulate payload data
			cumulation.addComponent(true, wrapper.retainedSlice());
			wrapper.readerIndex(wrapper.writerIndex());
			
			//op = 0 means cumulating ends up
			if(op == 0) {
				try {
					int num = cumulation.numComponents();
					
					packet.readPacketData(new PacketBuffer(cumulation));
					checkRemaining(cumulation, packet.getClass().getName());
					
					list.add(packet);
					
					if(SharedImpls.LOGGER_NETWORK.isDebugEnabled())
						SharedImpls.LOGGER_NETWORK.debug("=IN-POST	: with {} segment packets", num);
				} finally {
					cumulation.release();
					
					this.wild_packet = null;
					this.cumulation = null;
				}
			}
		}
	}
	
	@Unique
	private static void checkRemaining(ByteBuf buf, String pkt_name) throws IOException {
		if(buf.isReadable())
			throw new IOException("Wild packet " + pkt_name + " was larger than I expected, found " + buf.readableBytes() + " bytes extra");
	}
	
	@Unique
	private static void logDebug(EnumConnectionState state, int id) {
		if(LOGGER.isDebugEnabled()) {
			Class<?> klass = id == 24 ? SPacketCustomPayload.class : CPacketCustomPayload.class;
			
			LOGGER.debug(RECEIVED_PACKET_MARKER, "IN: [{}:{}] {}", state, id, klass.getName());
		}
	}
}