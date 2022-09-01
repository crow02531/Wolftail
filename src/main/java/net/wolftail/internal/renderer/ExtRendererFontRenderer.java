package net.wolftail.internal.renderer;

public interface ExtRendererFontRenderer {
	
	float wolftail_posX_get();
	
	float wolftail_posY_get();
	
	void wolftail_posX_set(float x);
	
	void wolftail_posY_set(float y);
	
	void wolftail_posX_add(float dx);
	
	void wolftail_posY_add(float dy);
	
	float wolftail_widthOf(int fontHeight, char codepoint);
	
	int wolftail_codeToColor(int i);
	
	char wolftail_randomReplacement(char codepoint);
	
	float wolftail_renderCodepoint(int fontHeight, char codepoint, boolean italic);
	
	void wolftail_renderAttachment(float width, boolean strikethrough, boolean underline);
}
