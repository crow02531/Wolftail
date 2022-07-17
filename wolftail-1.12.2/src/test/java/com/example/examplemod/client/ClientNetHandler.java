package com.example.examplemod.client;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.example.examplemod.network.C2SForward;

import net.minecraft.network.INetHandler;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.wolftail.api.ClientPlayContext;

public class ClientNetHandler implements INetHandler, ITickable {
	
	private final ClientPlayContext context;
	
	private boolean shift_pressed;
	private boolean ctrl_pressed;
	
	ClientNetHandler(ClientPlayContext context) {
		this.context = context;
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
	}
	
	@Override
	public void onDisconnect(ITextComponent reason) {
		System.err.println(reason.getFormattedText());
		
		ClientCallback.ui.release();
		ClientCallback.ui = null;
	}
}
