package net.wolftail.internal.renderer;

public interface ExtRendererFontRenderer {
	
	float wolftail_widthOf(int fontHeight, char codepoint);
	
	char wolftail_randomReplacement(char codepoint);
	
	float wolftail_renderCodepoint(int fontHeight, char codepoint, boolean italic);
	
	void wolftail_renderAttachment(float width, boolean strikethrough, boolean underline);
}
