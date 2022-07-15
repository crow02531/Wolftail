package net.wolftail.impl;

public interface ExtensionsFontRenderer {
	
	float wolftail_posX_get();
	float wolftail_posY_get();
	
	void wolftail_posX_set(float x);
	void wolftail_posY_set(float y);
	void wolftail_posX_add(float dx);
	void wolftail_posY_add(float dy);
	
	float wolftail_widthOf(int codepoint);
	
	int wolftail_codeToColor(int i);
	int wolftail_randomReplacement(int codepoint);
	
	float wolftail_renderCodepoint(int codepoint, boolean italic);
	void wolftail_renderAttachment(float width, boolean strikethrough, boolean underline);
}
