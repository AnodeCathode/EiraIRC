// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumChatFormatting;
import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.config.ChannelConfig;
import blay09.mods.eirairc.config.ServerConfig;
import blay09.mods.eirairc.util.Globals;

public class GuiChannelSlot extends GuiSlot {

	private final GuiChannelList parentGui;
	
	public GuiChannelSlot(GuiChannelList parentGui) {
		super(Minecraft.getMinecraft(), parentGui.width, parentGui.height, 32, parentGui.height - 64, 36);
		this.parentGui = parentGui;
	}

	@Override
	protected int getSize() {
		return parentGui.size();
	}

	@Override
	protected void elementClicked(int i, boolean flag) {
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
	protected void drawSlot(int i, int x, int y, int l, Tessellator tessellator) {
		ServerConfig serverConfig = parentGui.getServerConfig();
		ChannelConfig channelConfig = parentGui.getChannelConfig(i);
		String joinedString = null;
		if(EiraIRC.instance.isConnectedTo(serverConfig.getHost())) {
			if(EiraIRC.instance.getConnection(serverConfig.getHost()).getChannel(channelConfig.getName()) != null) {
				joinedString = EnumChatFormatting.GREEN + "Joined";
			} else {
				joinedString = EnumChatFormatting.RED + "Not Joined";
			}
		} else {
			joinedString = EnumChatFormatting.RED + "Not Connected";
		}
		
		parentGui.drawString(parentGui.getFontRenderer(), channelConfig.getName(), x + 2, y + 1, Globals.TEXT_COLOR);
		parentGui.drawString(parentGui.getFontRenderer(), joinedString, x + 4, y + 11, Globals.TEXT_COLOR);
	}

}
