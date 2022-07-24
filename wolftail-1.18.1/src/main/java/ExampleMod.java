import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.wolftail.api.IClientListener;
import net.wolftail.api.INetHandler;
import net.wolftail.api.IServerListener;
import net.wolftail.api.PlayContext;
import net.wolftail.api.UniversalPlayerTypeRegistry;

public class ExampleMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		
		UniversalPlayerTypeRegistry.INSTANCE.register(new ResourceLocation("modid", "pig"), new S(), new C());
	}
	
	private static class S implements IServerListener {
		
		@Override
		public void onEnter(PlayContext context) {
			context.setNetHandler(new INetHandler() {
				
				@Override
				public void handle(ByteBuf buf) {
					LOGGER.info(ByteBufUtil.prettyHexDump(buf));
					buf.readerIndex(buf.writerIndex());
				}
			});
		}
		
		@Override
		public void onLeave(PlayContext context) {
			
		}
	}
	
	private static class C implements IClientListener {

		@Override
		public void onEnter(PlayContext context) {
			ByteBuf buf = Unpooled.buffer();
			buf.writeInt(0xF0F);
			
			context.sendPacket(buf);
		}
		
		@Override
		public void onFrame() {
			RenderSystem.clearColor(0, 0, 0, 0);
			RenderSystem.clear(GlConst.GL_COLOR_BUFFER_BIT, Minecraft.ON_OSX);
			
			if(Minecraft.getInstance().screen == null)
				Minecraft.getInstance().mouseHandler.grabMouse();
		}
		
		@Override
		public void onLeave() {
			
		}
	}
}
