package net.blay09.mods.eirairc.client.gui;

import net.minecraft.client.gui.FontRenderer;

public abstract class GuiListTextEntry extends GuiListEntry {

	private FontRenderer fontRenderer;
	private int height;
	private String text;
	private int textColor;
	
	public GuiListTextEntry(FontRenderer fontRenderer, String text, int height, int textColor) {
		this.fontRenderer = fontRenderer;
		this.text = text;
		this.height = height;
		this.textColor = textColor;
	}
	

	@Override
	public void drawEntry(int x, int y) {
		drawString(fontRenderer, text, x + 4, y + height / 2 - fontRenderer.FONT_HEIGHT / 2, textColor);
	}
	
}
