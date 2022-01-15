package com.example.examplemod;

import java.util.Date;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.network.INetHandler;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
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
import net.wolftail.util.client.CmdUnit;

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
	
	public static class SNetHandler implements INetHandler, ITickable {
		
		private ServerPlayContext context;
		
		public EntityPig pig;
		
		private SNetHandler(ServerPlayContext c) {
			this.context = c;
			World w = c.manager().rootManager().server().worlds[0];
			
			this.pig = new EntityPig(w);
			this.pig.setLocationAndAngles(-12, 71, 2, 0, 0);
			this.pig.setCustomNameTag(c.playName());
			this.pig.setAlwaysRenderNameTag(true);
			w.spawnEntity(pig);
		}
		
		@Override
		public void onDisconnect(ITextComponent reason) {
			this.pig.onKillCommand();
		}
		
		@Override
		public void update() {
			if(this.pig.isDead) {
				this.context.disconnect();
			} else {
				this.pig.fallDistance = 0;
			}
		}
	}
	
	private static class FishS implements ServerEntryPoint {
		
		@Override
		public void onEnter(ServerPlayContext context) {
			context.setNetHandler(new SNetHandler(context));
		}
	}
	
	private static class CNetHandler implements INetHandler, ITickable {
		
		private final ClientPlayContext context;
		
		private boolean shift_pressed;
		private boolean ctrl_pressed;
		
		private CNetHandler(ClientPlayContext arg) {
			this.context = arg;
		}
		
		@Override
		public void update() {
			while(Keyboard.next()) {
				boolean state = Keyboard.getEventKeyState();
				
				switch(Keyboard.getEventKey()) {
				case Keyboard.KEY_ESCAPE:
					if(state) {
						this.context.disconnect();
						
						return;
					}
					
					break;
				case Keyboard.KEY_LSHIFT:
				case Keyboard.KEY_RSHIFT:
					this.shift_pressed = state;
					
					break;
				case Keyboard.KEY_LCONTROL:
				case Keyboard.KEY_RCONTROL:
					this.ctrl_pressed = state;
					
					break;
				case Keyboard.KEY_W:
					this.context.sendPacket(new C2SForward());
					
					break;
				default:
					if(state)
						; //FishC.println(Keyboard.getKeyName(Keyboard.getEventKey()));
				}
			}
			
			while(Mouse.next()) {
				int i = Mouse.getEventDWheel();
				
				if(i > 1) i = 1;
				else if(i < -1) i = -1;
				
				if(!this.shift_pressed) i *= 7;
				if(this.ctrl_pressed) i *= 7;
				
				FishC.ui.setScrollVertical(FishC.ui.getScrollVertical() - i);
			}
		}
		
		@Override
		public void onDisconnect(ITextComponent reason) {
			FishC.ui.release();
		}
	}
	
	private static class FishC implements ClientEntryPoint, ClientFrameCallback {
		
		private static CmdUnit ui;
		
		@Override
		public void onFrame(ClientPlayContext context) {
			CmdUnit cmd = ui;
			
			if(Display.wasResized()) {
				Minecraft mc = Minecraft.getMinecraft();
				
				cmd.resize(mc.displayWidth, mc.displayHeight);
			}
			
			GL11.glClearColor(0, 0, 0, 1);
			GL11.glClearDepth(1);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, 1, 1, 0, -1, 1);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();
			
			StringBuilder s = new StringBuilder();
			
			s.append("Playing...\n");
			s.append(new Date());
			
			for(int i = 0; i < 100; ++i) {
				s.append(i).append('\n');
			}
			
			cmd.useDoc(s.toString());
			
			cmd.flush();
			cmd.render(new Vector3f(0, 1, 0), new Vector3f(1, 1, 0), new Vector3f(1, 0, 0), new Vector3f(0, 0, 0));
			//cmd.render(new Vector3f(0, 1, 0), new Vector3f(1, 1, 0), new Vector3f(1, 0.2f, 0), new Vector3f(0, 0.5f, 0));
		}
		
		@Override
		public void onEnter(ClientPlayContext context) {
			context.setNetHandler(new CNetHandler(context));
			
			ui = new CmdUnit(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, calcPPU());
		}
		
		private static final int calcPPU() {
			return 18; //Toolkit.getDefaultToolkit().getScreenResolution();
		}
	}
}
