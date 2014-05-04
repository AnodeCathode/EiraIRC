// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.settings;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.client.gui.GuiAdvancedTextField;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import org.lwjgl.input.Keyboard;

public class GuiChannelConfig extends GuiScreen {

	private static final int BUTTON_WIDTH = 140;
	private static final int BUTTON_HEIGHT = 20;
	
	private final ServerConfig serverConfig;
	private final GuiScreen listParentScreen;
	private ChannelConfig config;
	private GuiButton btnCancel;
	private GuiButton btnSave;
	private GuiButton btnAutoJoin;
	private GuiButton btnAutoWho;
	
	private GuiTextField txtName;
	private GuiAdvancedTextField txtChannelPassword;
	
	private boolean autoJoin;
	private boolean autoWho;
	
	public GuiChannelConfig(GuiScreen listParentScreen, ServerConfig serverConfig) {
		this.listParentScreen = listParentScreen;
		this.serverConfig = serverConfig;
	}
	
	public GuiChannelConfig(GuiScreen listParentScreen, ChannelConfig config) {
		this.listParentScreen = listParentScreen;
		this.config = config;
		serverConfig = config.getServerConfig();
	}
	
	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		txtName = new GuiTextField(fontRendererObj, width / 2 - 106, height / 2 - 85, 100, 15);
		txtChannelPassword = new GuiAdvancedTextField(fontRendererObj, width / 2 + 6, height / 2 - 85, 100, 15);
		txtChannelPassword.setDefaultPasswordChar();
		
		btnAutoJoin = new GuiButton(3, width / 2 + 3, height / 2 - 65, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnAutoJoin);
		
		btnAutoWho = new GuiButton(10, width / 2 + 3, height / 2 + 10, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnAutoWho);
		
		btnSave = new GuiButton(1, width / 2 + 3, height / 2 + 65, 100, 20, Utils.getLocalizedMessage("irc.gui.save"));
		buttonList.add(btnSave);
		
		btnCancel = new GuiButton(0, width / 2 - 103, height / 2 + 65, 100, 20, Utils.getLocalizedMessage("irc.gui.cancel"));
		buttonList.add(btnCancel);
		
		loadFromConfig();
	}
	
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
	
	@Override
	public void updateScreen() {
		txtName.updateCursorCounter();
		txtChannelPassword.updateCursorCounter();
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		drawCenteredString(fontRendererObj, Utils.getLocalizedMessage("irc.gui.editChannel"), width / 2, height / 2 - 115, Globals.TEXT_COLOR);
		fontRendererObj.drawString(Utils.getLocalizedMessage("irc.gui.editChannel.name"), width / 2 - 106, height / 2 - 100, Globals.TEXT_COLOR);
		txtName.drawTextBox();
		fontRendererObj.drawString(Utils.getLocalizedMessage("irc.gui.editChannel.password"), width / 2 + 6, height / 2 - 100, Globals.TEXT_COLOR);
		txtChannelPassword.drawTextBox();
		super.drawScreen(par1, par2, par3);
	}
	
	@Override
	public void keyTyped(char unicode, int keyCode) {
		super.keyTyped(unicode, keyCode);
		if(txtName.textboxKeyTyped(unicode, keyCode)) {
			if(txtName.getText().length() > 0) {
				btnSave.enabled = true;
			} else {
				btnSave.enabled = false;
			}
			return;
		}
		if(txtChannelPassword.textboxKeyTyped(unicode, keyCode)) {
			return;
		}
	}
	
	@Override
	public void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);
		txtName.mouseClicked(par1, par2, par3);
		txtChannelPassword.mouseClicked(par1, par2, par3);
	}
	
	private void updateButtons() {
		if(txtName.getText().length() > 0) {
			btnSave.enabled = true;
		} else {
			btnSave.enabled = false;
		}
		final String yes = Utils.getLocalizedMessage("irc.gui.yes");
		final String no = Utils.getLocalizedMessage("irc.gui.no");
		btnAutoJoin.displayString = Utils.getLocalizedMessage("irc.gui.config.joinStartup", (autoJoin ? yes : no));
		btnAutoWho.displayString = Utils.getLocalizedMessage("irc.gui.editChannel.autoWho", (autoWho ? yes : no));
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnSave) {
			saveToConfig();
			if(autoJoin && EiraIRC.instance.isConnectedTo(serverConfig.getHost())) {
				EiraIRC.instance.getConnection(serverConfig.getHost()).join(config.getName(), config.getPassword());
			}
			Minecraft.getMinecraft().displayGuiScreen(new GuiChannelList(listParentScreen, serverConfig));
		} else if(button == btnCancel) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiChannelList(listParentScreen, serverConfig));
		} else if(button == btnAutoJoin) {
			autoJoin = !autoJoin;
			updateButtons();
		} else if(button == btnAutoWho) {
			autoWho = !autoWho;
			updateButtons();
		}
	}
	
	public void loadFromConfig() {
		if(config != null) {
			txtName.setText(config.getName());
			txtChannelPassword.setText(config.getPassword() != null ? config.getPassword() : "");
			autoJoin = config.isAutoJoin();
			autoWho = config.isAutoWho();
		} else {
			autoJoin = true;
			autoWho = false;
		}
		updateButtons();
	}
	
	public void saveToConfig() {
		if(config == null || !config.getName().equals(txtName.getText())) {
			if(config != null) {
				serverConfig.removeChannelConfig(config.getName());
			}
			config = serverConfig.getChannelConfig(txtName.getText());
		}
		config.setPassword(txtChannelPassword.getText());
		config.setAutoJoin(autoJoin);
		config.setAutoWho(autoWho);
		serverConfig.addChannelConfig(config);
		ConfigurationHandler.save();
	}
}
