// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.screenshot;

import net.blay09.mods.eirairc.client.screenshot.Screenshot;
import net.blay09.mods.eirairc.client.screenshot.ScreenshotThumbnail;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;

import net.minecraft.client.renderer.WorldRenderer;
import org.lwjgl.opengl.GL11;

public class GuiScreenshotSlot extends GuiSlot {

	private final GuiScreenshotList parentGui;
	private final Minecraft mc;
	
	public GuiScreenshotSlot(GuiScreenshotList parentGui) {
		super(Minecraft.getMinecraft(), parentGui.width, parentGui.height, 32, parentGui.height - 64, 36);
		mc = Minecraft.getMinecraft();
		this.parentGui = parentGui;
	}

	@Override
	protected int getSize() {
		return parentGui.size();
	}

	@Override
	protected void elementClicked(int i, boolean flag, int j, int k) {
		parentGui.onElementSelected(i);
		if(flag) {
			parentGui.onElementClicked(i);
		}
	}

	@Override
	protected boolean isSelected(int i) {
		return parentGui.getSelectedElement() == i;
	}

	@Override
	protected void drawBackground() {
		parentGui.drawDefaultBackground();
	}
	
	protected int getContentHeight() {
		return getSize() * 36;
	}

	@Override
	protected void drawSlot(int i, int x, int y, int l, int k, int j) {
		Screenshot screenshot = parentGui.getScreenshot(i);
		float f = 0.00390625F * 2;
		float f1 = 0.00390625F * 4;
		int thumbWidth = 64;
		int thumbHeight = 32;
		mc.getTextureManager().bindTexture(screenshot.getThumbnail().getResourceLocation());
		GL11.glColor4f(1f, 1f, 1f, 1f);
		WorldRenderer renderer = Tessellator.getInstance().getWorldRenderer();
		renderer.startDrawingQuads();
		renderer.addVertexWithUV(x, y + thumbHeight, 0, 0, ScreenshotThumbnail.HEIGHT * f1);
		renderer.addVertexWithUV(x + thumbWidth, y + thumbHeight, 0, ScreenshotThumbnail.WIDTH * f, ScreenshotThumbnail.HEIGHT * f1);
		renderer.addVertexWithUV(x + thumbWidth, y, 0, ScreenshotThumbnail.WIDTH * f, 0);
		renderer.addVertexWithUV(x, y, 0, 0, 0);
		renderer.draw();
		parentGui.drawString(parentGui.getFontRenderer(), screenshot.getName(), x + thumbWidth + 6, y + 1, Globals.TEXT_COLOR);
		String sharedString = Utils.getLocalizedMessage("irc.gui.screenshots.local");
		if(screenshot.isUploaded()) {
			sharedString = Utils.getLocalizedMessage("irc.gui.screenshots.uploaded");
		}
		parentGui.drawString(parentGui.getFontRenderer(), sharedString, x + thumbWidth + 8, y + 12, Globals.TEXT_COLOR);
	}

}
