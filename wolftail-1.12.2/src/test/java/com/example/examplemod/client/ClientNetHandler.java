package com.example.examplemod.client;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.example.examplemod.network.C2SForward;

import net.minecraft.network.INetHandler;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.wolftail.api.ClientPlayContext;
import net.wolftail.util.client.renderer.CmdUnit;
import net.wolftail.util.tracker.SlaveTime;
import net.wolftail.util.tracker.SlaveUniverse;
import net.wolftail.util.tracker.SlaveWeather;
import net.wolftail.util.tracker.SlaveWorld;

public class ClientNetHandler implements INetHandler, ITickable {
	
	private final ClientPlayContext context;
	
	private boolean shift_pressed;
	private boolean ctrl_pressed;
	
	public SlaveUniverse universe;
	
	ClientNetHandler(ClientPlayContext context) {
		this.context = context;
		
		this.universe = new SlaveUniverse();
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
			}
		}
		
		while(Mouse.next()) {
			float i = -Mouse.getEventDWheel();
			
			if(i > 1) i = 1F/9F;
			else if(i < -1) i = -1F/9F;
			
			if(!this.shift_pressed) i *= 9F;
			if(this.ctrl_pressed) i *= 9F;
			
			ClientCallback.ui.pScrollMov(i);
		}
		
		SlaveWorld w = this.universe.world(DimensionType.OVERWORLD);
		SlaveWeather weather;
		SlaveTime time;
		
		if(w != null && (weather = w.weather()) != null && (time = w.time()) != null) {
			CmdUnit ui = ClientCallback.ui;
			
			if(timer + 3000 < System.currentTimeMillis()) {
				ui.pPrint(TextFormatting.YELLOW).pPrintln(w.chunk(0, 0).blockState(4, 5, 4));
				ui.pPrintln(weather.rainingStrength());
				ui.pPrintln(weather.thunderingStrength());
				ui.pPrintln(time.dayTime());
				ui.pPrintln();
				
				timer = System.currentTimeMillis();
			}
		}
	}
	
	private long timer;
	
	@Override
	public void onDisconnect(ITextComponent reason) {
		System.err.println(reason.getFormattedText());
		
		ClientCallback.ui.release();
		ClientCallback.ui = null;
	}
}
