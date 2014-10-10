package net.blay09.mods.eirairc.client.gui.base.tab;

import net.blay09.mods.eirairc.client.gui.EiraGuiScreen;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Blay09 on 04.10.2014.
 */
public class GuiTabContainer extends EiraGuiScreen {

	private static final ResourceLocation tabHeader = new ResourceLocation("eirairc", "gfx/tab.png");

	private final List<GuiTabHeader> headers = new ArrayList<GuiTabHeader>();
	protected final List<GuiTabPage> pages = new ArrayList<GuiTabPage>();

	protected GuiTabPage currentTab;
	protected int panelWidth;
	protected int panelHeight;

	public GuiTabContainer(GuiScreen parentScreen) {
		super(parentScreen);
	}

	@Override
	public void initGui() {
		super.initGui();

		panelWidth = 300;
		panelHeight = 190;

		if(currentTab != null) {
			currentTab.setWorldAndResolution(mc, width, height);
			currentTab.initGui();
		}
	}

	protected void buildHeaders() {
		headers.clear();
		int curX = menuX;
		int headerY = menuY - 8;
		for(int i = 0; i < pages.size(); i++) {
			int titleWidth = Math.max(4, fontRendererObj.getStringWidth(pages.get(i).getTitle()) - 8);
			headers.add(new GuiTabHeader(pages.get(i), curX, headerY, titleWidth + 32, 16));
			curX += titleWidth + 24;
		}
	}

	public void setCurrentTab(GuiTabPage tabPage, boolean forceClose) {
		if(currentTab == tabPage) {
			return;
		}
		if(currentTab != null) {
			if(!forceClose && !currentTab.requestClose()) {
				return;
			}
			currentTab.onGuiClosed();
		}
		currentTab = tabPage;
		if(currentTab != null) {
			currentTab.setWorldAndResolution(mc, width, height);
			currentTab.initGui();
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		for(int i = 0; i < headers.size(); i++) {
			GuiTabHeader header = headers.get(i);

			if(mouseX >= header.x && mouseX < header.x + header.width - 8 && mouseY >= header.y && mouseY < header.y + header.height) {
				header.tabPage.tabClicked();
			}
		}

		if(currentTab != null) {
			currentTab.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	public void gotoPrevious() {
		if(currentTab.getParentScreen() instanceof GuiTabPage) {
			setCurrentTab((GuiTabPage) currentTab.getParentScreen(), false);
		} else {
			super.gotoPrevious();
		}
	}

	@Override
	public void keyTyped(char unicode, int keyCode) {
		super.keyTyped(unicode, keyCode);

		if(currentTab != null) {
			currentTab.keyTyped(unicode, keyCode);
		}
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();

		if(currentTab != null) {
			currentTab.requestClose();
			currentTab.onGuiClosed();
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		super.drawScreen(mouseX, mouseY, par3);
		mc.renderEngine.bindTexture(tabHeader);
		drawTexturedRect(menuX, menuY + 8, panelWidth - 16, panelHeight - 16, 0, 16, 16, 16, 256, 256);
		drawTexturedRect(menuX + 16, menuY + 8 + panelHeight - 16, panelWidth - 32, 16, 0, 16, 16, 16, 256, 256);
		drawTexturedRect(menuX + panelWidth - 16, menuY + 24, 16, panelHeight - 32, 0, 16, 16, 16, 256, 256);
		drawTexturedRect(menuX, menuY + 8 + panelHeight - 16, 16, 16, 0, 32, 16, 16, 256, 256);
		drawTexturedRect(menuX + panelWidth - 16, menuY + 8 + panelHeight - 16, 16, 16, 16, 32, 16, 16, 256, 256);
		drawTexturedRect(menuX + panelWidth - 16, menuY + 8, 16, 16, 16, 16, 16, 16, 256, 256);

		if(currentTab != null) {
			currentTab.drawScreen(mouseX, mouseY, par3);
		}

		GuiTabHeader currentHeader = null;
		for(int i = headers.size() - 1; i >= 0; i--) {
			GuiTabHeader header = headers.get(i);

			if(currentTab != null && (header.tabPage == currentTab || header.tabPage == currentTab.getParentScreen())) {
				currentHeader = header;
			} else {
				header.draw(mouseX, mouseY, false);
			}
		}
		if(currentHeader != null) {
			currentHeader.draw(mouseX, mouseY, true);
		}
	}

	public void removePage(GuiTabPage tabPage) {
		pages.remove(tabPage);
		if(tabPage == currentTab) {
			if(pages.size() > 0) {
				setCurrentTab(pages.get(0), true);
			} else {
				setCurrentTab(null, true);
			}
		}
	}
}
