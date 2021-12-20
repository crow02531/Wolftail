package com.example.examplemod;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Blocks;
import net.minecraft.network.INetHandler;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.wolftail.api.ClientEntryPoint;
import net.wolftail.api.ClientFrameCallback;
import net.wolftail.api.ClientPlayContext;
import net.wolftail.api.ServerEntryPoint;
import net.wolftail.api.ServerPlayContext;
import net.wolftail.api.UniversalPlayerType;
import net.wolftail.api.UniversalPlayerTypeRegistry;

@Mod(modid = ExampleMod.MODID, name = ExampleMod.NAME, version = ExampleMod.VERSION)
public class ExampleMod {
	
	public static final String MODID = "examplemod";
	public static final String NAME = "Example Mod";
	public static final String VERSION = "1.0";
	
	@EventHandler
    public void init(FMLInitializationEvent event) {
		FishC c = new FishC();
		UniversalPlayerType type_fishes = UniversalPlayerType.create(new FishS(), c, c);
		
		UniversalPlayerTypeRegistry.INSTANCE.register(new ResourceLocation("examplemod", "fishes"), type_fishes);
	}
	
	private static class SNetHandler implements INetHandler, ITickable {
		
		private final ServerPlayContext context;
		private final World overworld;
		
		private BlockPos position;
		
		private SNetHandler(ServerPlayContext arg) {
			this.overworld = (this.context = arg).manager().rootManager().server().worlds[0];
			
			this.position = BlockPos.ORIGIN;
		}
		
		@Override
		public void update() {
			this.overworld.setBlockState(this.position, Blocks.LIT_PUMPKIN.getDefaultState());
		}
		
		@Override
		public void onDisconnect(ITextComponent reason) {
			
		}
	}
	
	private static class CNetHandler implements INetHandler, ITickable {
		
		private final ClientPlayContext context;
		
		private CNetHandler(ClientPlayContext arg) {
			this.context = arg;
		}
		
		@Override
		public void update() {
			while(Keyboard.next()) {
				if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
					this.context.disconnect();
				}
			}
		}
		
		@Override
		public void onDisconnect(ITextComponent reason) {
			
		}
	}
	
	private static class FishS implements ServerEntryPoint {
		
		@Override
		public void onEnter(ServerPlayContext context) {
			context.setNetHandler(new SNetHandler(context));
		}
	}
	
	private static class FishC implements ClientEntryPoint, ClientFrameCallback {
		
		@Override
		public void onFrame(ClientPlayContext context) {
			Minecraft mc = Minecraft.getMinecraft();
			ScaledResolution scaledresolution = new ScaledResolution(mc);
			
			GL11.glClearColor(0, 0, 0, 1);
			GL11.glClearDepth(1);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();
			GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
			
			Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Playing...", 1, 1, -1);
		}
		
		@Override
		public void onEnter(ClientPlayContext context) {
			context.setNetHandler(new CNetHandler(context));
		}
	}
}
