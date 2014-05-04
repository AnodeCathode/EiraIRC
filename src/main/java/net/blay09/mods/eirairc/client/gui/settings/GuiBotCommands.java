// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.settings;

import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.bot.BotCommandCustom;
import net.blay09.mods.eirairc.client.gui.GuiAdvancedTextField;
import net.blay09.mods.eirairc.client.gui.GuiList;
import net.blay09.mods.eirairc.client.gui.GuiListTextEntry;
import net.blay09.mods.eirairc.config.BotProfile;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;

public class GuiBotCommands extends GuiScreen {

	private static final int BUTTON_WIDTH = 150;
	private static final int BUTTON_HEIGHT = 20;

	private final GuiScreen parentScreen;
	private final BotProfile botProfile;
	
	private GuiButton btnInterOp;
	private GuiAdvancedTextField txtDisabledInterOpCommands;
	private GuiAdvancedTextField txtDisabledCommands;
	private GuiList listCommands;
	private GuiButton btnBack;
	
	public GuiBotCommands(GuiScreen parentScreen, BotProfile botProfile) {
		this.parentScreen = parentScreen;
		this.botProfile = botProfile;
	}
	
	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		int leftX = width /  2 - 172;
		int rightX = width / 2 + 2;
		int topY = height / 2 - 60;
		
		btnInterOp = new GuiButton(1, leftX, topY - 6, BUTTON_WIDTH, BUTTON_HEIGHT, "InterOp: ???");
		btnInterOp.enabled = false;
		buttonList.add(btnInterOp);

		txtDisabledInterOpCommands = new GuiAdvancedTextField(fontRendererObj, rightX, topY + 2, BUTTON_WIDTH, 16);
		txtDisabledInterOpCommands.setEnabled(false);
		txtDisabledCommands = new GuiAdvancedTextField(fontRendererObj, rightX, topY + 42, BUTTON_WIDTH, 16);
		txtDisabledCommands.setDefaultText("Example: who, players", true);
		
		listCommands = new GuiList(leftX, topY + 40, BUTTON_WIDTH, 100, 18);
		
		btnBack = new GuiButton(0, width / 2 - 100, topY + 150, 200, 20, Utils.getLocalizedMessage("irc.gui.back"));
		buttonList.add(btnBack);

		loadFromProfile();
	}
	
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
	
	private void loadFromProfile() {
		btnInterOp.displayString = Utils.getLocalizedMessage("irc.gui.config.interOp", Utils.getLocalizedMessage((botProfile.isInterOp() ? "irc.gui.yes" : "irc.gui.no")));
		if(!botProfile.isInterOp()) {
			txtDisabledInterOpCommands.setDefaultText("InterOp is not enabled.", true);
		}
		for(IBotCommand command : botProfile.getCommands()) {
			if(command instanceof BotCommandCustom) {
				listCommands.addEntry(new GuiListTextEntry(fontRendererObj, command.getCommandName(), listCommands.getEntryHeight(), Globals.TEXT_COLOR) {
					@Override
					public void setSelected(boolean selected) {
						
					}
				});
			}
		}
		listCommands.addEntry(new GuiListTextEntry(fontRendererObj, "Add...", listCommands.getEntryHeight(), -16711936) {
			@Override
			public void setSelected(boolean selected) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiBotCommand(GuiBotCommands.this, botProfile, new BotCommandCustom()));
			}
		});
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnBack) {
			Minecraft.getMinecraft().displayGuiScreen(parentScreen);
			return;
		}
	}
	
	@Override
	public void updateScreen() {
		txtDisabledCommands.updateCursorCounter();
	}
	
	@Override
	public void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);
		txtDisabledCommands.mouseClicked(par1, par2, par3);
		if(txtDisabledInterOpCommands.isEnabled()) {
			txtDisabledInterOpCommands.mouseClicked(par1, par2, par3);
		}
		listCommands.mouseClicked(par1, par2, par3);
	}
	
	@Override
	public void keyTyped(char unicode, int keyCode) {
		super.keyTyped(unicode, keyCode);
		if(txtDisabledCommands.textboxKeyTyped(unicode, keyCode)) {
			return;
		}
		if(txtDisabledInterOpCommands.textboxKeyTyped(unicode, keyCode)) {
			return;
		}
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		this.drawCenteredString(fontRendererObj, Utils.getLocalizedMessage("irc.gui.botCommands"), width / 2, height / 2 - 110, Globals.TEXT_COLOR);
		drawString(fontRendererObj, "Disabled InterOp Commands:", width / 2, height / 2 - 75, Globals.TEXT_COLOR);
		txtDisabledInterOpCommands.drawTextBox();
		drawString(fontRendererObj, "Disabled Bot Commands:", width / 2, height / 2 - 34, Globals.TEXT_COLOR);
		txtDisabledCommands.drawTextBox();
		drawString(fontRendererObj, "An asterisk ('*') disables all commands.", width / 2, height / 2 + 6, Globals.TEXT_COLOR);
		drawString(fontRendererObj, "Custom Bot Commands:", width / 2 - 170, height / 2 - 34, Globals.TEXT_COLOR);
		listCommands.drawList();
		super.drawScreen(par1, par2, par3);
	}
}
