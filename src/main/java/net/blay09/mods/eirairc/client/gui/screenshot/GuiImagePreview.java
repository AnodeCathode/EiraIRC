package net.blay09.mods.eirairc.client.gui.screenshot;

import net.blay09.mods.eirairc.client.gui.EiraGui;
import net.blay09.mods.eirairc.client.gui.EiraGuiScreen;
import net.blay09.mods.eirairc.client.gui.base.GuiImageButton;
import net.blay09.mods.eirairc.client.gui.base.image.GuiImage;
import net.blay09.mods.eirairc.client.gui.base.image.GuiURLImage;
import net.blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiYesNoCallback;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Blay09 on 04.10.2014.
 */
public class GuiImagePreview extends EiraGuiScreen implements GuiYesNoCallback {

	private static final float TOOLTIP_TIME = 30;

	private final GuiImage imgPreview;
	private final URL url;

	private GuiImageButton btnZoom;
	private GuiImageButton btnClipboard;
	private GuiImageButton btnGoToURL;

	private int imgX;
	private int imgY;
	private final int imgWidth = 285;
	private final int imgHeight = 180;

	private boolean buttonsVisible;
	private float hoverTime;
	private GuiImageButton hoverObject;

	public GuiImagePreview(URL url) {
		super(null);
		this.url = url;
		imgPreview = new GuiURLImage(url);
		imgPreview.loadTexture();
	}

	@Override
	public void initGui() {
		super.initGui();

		final int leftX = width / 2 - 145;
		final int rightX = width / 2 + 145;
		final int topY = height / 2 - 100;

		btnZoom = new GuiImageButton(0, rightX - 37, topY + 12, EiraGui.atlas.findRegion("button_zoom"));
		btnZoom.setTooltipText("View Fullscreen");
		buttonList.add(btnZoom);

		btnGoToURL = new GuiImageButton(1, rightX - 37, topY + 50, EiraGui.atlas.findRegion("button_upload"));
		btnGoToURL.setTooltipText("Open in Browser");
		buttonList.add(btnGoToURL);

		btnClipboard = new GuiImageButton(2, rightX - 37, topY + 88, EiraGui.atlas.findRegion("button_clipboard"));
		btnClipboard.setTooltipText("URL to Clipboard");
		buttonList.add(btnClipboard);

		imgX = leftX + 2;
		imgY = topY + 10;
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnClipboard) {
			Utils.setClipboardString(url.toString());
		} else if(button == btnZoom) {
			mc.displayGuiScreen(new GuiScreenshotBigPreview(this, imgPreview));
		} else if(button == btnGoToURL) {
			mc.displayGuiScreen(new GuiConfirmOpenLink(this, url.toString(), 0, true));
		}
	}

	@Override
	public void confirmClicked(boolean result, int id) {
		if(result) {
			Utils.openWebpage(url);
			Minecraft.getMinecraft().displayGuiScreen(null);
		} else {
			Minecraft.getMinecraft().displayGuiScreen(this);
		}
	}

	private static final List<String> tooltipList = new ArrayList<String>();
	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		drawLightBackground(menuX, menuY, menuWidth, menuHeight);

		// Fade all image buttons in/out on hover of image
		if(imgPreview != null && mouseX >= imgX && mouseX < imgX + imgWidth && mouseY >= imgY && mouseY < imgY + imgHeight) {
			if(!buttonsVisible) {
				for (int i = 0; i < buttonList.size(); i++) {
					GuiButton button = (GuiButton) buttonList.get(i);
					if (button instanceof GuiImageButton) {
						((GuiImageButton) button).setFadeMode(1);
					}
				}
				buttonsVisible = true;
			}
		} else {
			if (buttonsVisible) {
				for (int i = 0; i < buttonList.size(); i++) {
					GuiButton button = (GuiButton) buttonList.get(i);
					if (button instanceof GuiImageButton) {
						((GuiImageButton) button).setFadeMode(-1);
					}
				}
				buttonsVisible = false;
			}
		}

		if(imgPreview != null) {
			// Render the preview image
			imgPreview.draw(imgX, imgY, imgWidth, imgHeight, zLevel);
		}

		super.drawScreen(mouseX, mouseY, par3);

		for (int i = 0; i < buttonList.size(); i++) {
			GuiButton button = (GuiButton) buttonList.get(i);
			if (button instanceof GuiImageButton) {
				GuiImageButton imageButton = (GuiImageButton) button;
				if(imageButton.isInside(mouseX, mouseY) && imageButton.visible && imageButton.isAlphaVisible() && imageButton.getTooltipText() != null) {
					if(imageButton != hoverObject) {
						hoverObject = imageButton;
						hoverTime = 0f;
					}
					hoverTime++;
					if(hoverTime > TOOLTIP_TIME) {
						tooltipList.clear();
						tooltipList.add(imageButton.getTooltipText());
						func_146283_a(tooltipList, mouseX, mouseY);
					}
					break;
				}
			}
		}
	}

}