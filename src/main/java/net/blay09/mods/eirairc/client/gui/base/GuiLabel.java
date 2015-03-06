package net.blay09.mods.eirairc.client.gui.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

/**
 * Created by Blay09 on 04.10.2014.
 */
public class GuiLabel extends Gui {

	private static final int LINE_SPACING = 3;

	private final Minecraft mc;
	private final int posX;
	private final int posY;
	private final String[] lines;
	private final int color;
	private HAlignment hAlign = HAlignment.Left;
	private int alignWidth;

	public static enum HAlignment {
		Left,
		Center,
		Right
	}

	public GuiLabel(String text, int posX, int posY, int color) {
		this.mc = Minecraft.getMinecraft();
		this.lines = text.split("\n");
		this.posX = posX;
		this.posY = posY;
		this.color = color;
	}

	public void setHAlignment(HAlignment hAlign, int alignWidth) {
		this.hAlign = hAlign;
		this.alignWidth = alignWidth;
	}

	public void drawLabel() {
		for(int i = 0; i < lines.length; i++) {
			int textWidth = mc.fontRendererObj.getStringWidth(lines[i]);
			int offX = 0;
			switch(hAlign) {
				case Left: offX = 0; break;
				case Center: offX = alignWidth / 2 - textWidth / 2; break;
				case Right: offX = alignWidth - textWidth; break;
			}
			drawString(mc.fontRendererObj, lines[i], posX + offX, posY + i * (mc.fontRendererObj.FONT_HEIGHT + LINE_SPACING), color);
		}
	}

}
