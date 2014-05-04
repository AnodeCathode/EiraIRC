// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.settings;

import java.util.List;

import net.blay09.mods.eirairc.client.gui.GuiAdvancedTextField;
import net.blay09.mods.eirairc.config.BotProfile;
import net.blay09.mods.eirairc.config.DisplayFormatConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;

public class GuiBotSettings extends GuiScreen {

	private static final int BUTTON_WIDTH = 170;
	private static final int BUTTON_HEIGHT = 20;
	
	private GuiButton btnPrevProfile;
	private GuiButton btnCurrentProfile;
	private GuiAdvancedTextField txtCurrentProfile;
	private GuiButton btnNextProfile;
	private GuiButton btnNewProfile;
	private GuiButton btnDeleteProfile;
	
	private GuiButton btnRelayDeathMessages;
	private GuiButton btnRelayMCJoinLeave;
	private GuiButton btnRelayIRCJoinLeave;
	private GuiButton btnRelayNickChanges;
	private GuiButton btnRelayBroadcasts;
	private GuiButton btnMuted;
	private GuiButton btnReadOnly;
	private GuiButton btnLinkFilter;
	private GuiButton btnDisplayFormat;
	private GuiButton btnAllowPrivateMessages;
	private GuiButton btnAutoWho;
	
	private GuiButton btnCommands;
	private GuiButton btnMacros;
	private GuiButton btnBack;
	
	private final GuiScreen parentScreen;
	private final String customName;
	private final List<BotProfile> profileList;
	private BotProfile currentProfile;
	private int currentIdx;
	
	private final List<DisplayFormatConfig> displayFormatList;
	private int currentDisplayFormatIdx;
	
	public GuiBotSettings(GuiScreen parentScreen) {
		this(parentScreen, "custom");
	}
	
	public GuiBotSettings(GuiScreen parentScreen, String customName) {
		this(parentScreen, customName, !ConfigurationHandler.getBotProfiles().isEmpty() ? ConfigurationHandler.getBotProfiles().get(0) : null);
	}
	
	public GuiBotSettings(GuiScreen parentScreen, String customName, BotProfile initialProfile) {
		this.parentScreen = parentScreen;
		this.customName = customName;
		displayFormatList = ConfigurationHandler.getDisplayFormats();
		profileList = ConfigurationHandler.getBotProfiles();
		if(initialProfile != null) {
			for(int i = 0; i < profileList.size(); i++) {
				if(profileList.get(i) == initialProfile) {
					currentIdx = i;
					currentProfile = initialProfile;
					break;
				}
			}
		}
	}
	
	@Override
	public void initGui() {
		int leftX = width /  2 - 172;
		int rightX = width / 2 + 2;
		int topY = height / 2 - 60;
		
		// Profile Browser
		
		btnCurrentProfile = new GuiButton(1, width / 2 - 50, height / 2 - 90, 100, BUTTON_HEIGHT, "");
		btnCurrentProfile.enabled = true;
		buttonList.add(btnCurrentProfile);
		
		txtCurrentProfile = new GuiAdvancedTextField(fontRendererObj, btnCurrentProfile.xPosition + 5, btnCurrentProfile.yPosition + BUTTON_HEIGHT / 2 - fontRendererObj.FONT_HEIGHT / 2, 100, BUTTON_HEIGHT);
		txtCurrentProfile.setEnableBackgroundDrawing(false);
		txtCurrentProfile.setTextCentered(true);
		txtCurrentProfile.setEnabled(false);
		
		btnPrevProfile = new GuiButton(2, width / 2 - 72, height / 2 - 90, 20, BUTTON_HEIGHT, "<");
		buttonList.add(btnPrevProfile);
		
		btnNextProfile = new GuiButton(3, width / 2 + 52, height / 2 - 90, 20, BUTTON_HEIGHT, ">");
		buttonList.add(btnNextProfile);
		
		btnNewProfile = new GuiButton(4, width / 2 + 74, height / 2 - 90, 20, BUTTON_HEIGHT, "+");
		buttonList.add(btnNewProfile);
		
		btnDeleteProfile = new GuiButton(5, width / 2 - 94, height / 2 - 90, 20, BUTTON_HEIGHT, "-");
		buttonList.add(btnDeleteProfile);
		
		// Relay Options
		
		btnRelayDeathMessages = new GuiButton(6, leftX, topY, BUTTON_WIDTH, BUTTON_HEIGHT, "Death Messages: ???");
		buttonList.add(btnRelayDeathMessages);
		
		btnRelayMCJoinLeave = new GuiButton(7, leftX, topY + 25, BUTTON_WIDTH, BUTTON_HEIGHT, "Minecraft Join/Leave: ???");
		buttonList.add(btnRelayMCJoinLeave);
		
		btnRelayBroadcasts = new GuiButton(8, leftX, topY + 50, BUTTON_WIDTH, BUTTON_HEIGHT, "Server Broadcasts: ???");
		buttonList.add(btnRelayBroadcasts);
		
		btnRelayIRCJoinLeave = new GuiButton(9, leftX, topY + 75, BUTTON_WIDTH, BUTTON_HEIGHT, "IRC Join/Leave: ???");
		buttonList.add(btnRelayIRCJoinLeave);
		
		btnRelayNickChanges = new GuiButton(10, leftX, topY + 100, BUTTON_WIDTH, BUTTON_HEIGHT, "IRC Nick Changes: ???");
		buttonList.add(btnRelayNickChanges);
		
		// Other Options
		
		btnLinkFilter = new GuiButton(11, rightX, topY, BUTTON_WIDTH, BUTTON_HEIGHT, "Filter Links: ???");
		buttonList.add(btnLinkFilter);
		
		btnAutoWho = new GuiButton(12, rightX, topY + 25, BUTTON_WIDTH, BUTTON_HEIGHT, "Auto-Who: ???");
		buttonList.add(btnAutoWho);
		
		btnAllowPrivateMessages = new GuiButton(13, rightX, topY + 50, BUTTON_WIDTH, BUTTON_HEIGHT, "Private Messages: ???");
		buttonList.add(btnAllowPrivateMessages);
		
		btnDisplayFormat = new GuiButton(14, rightX, topY + 75, BUTTON_WIDTH, BUTTON_HEIGHT, "Display Format: ???");
		buttonList.add(btnDisplayFormat);
		
		btnCommands = new GuiButton(15, rightX, topY + 100, BUTTON_WIDTH, BUTTON_HEIGHT, "Commands");
		buttonList.add(btnCommands);
		
		btnBack = new GuiButton(0, width / 2 - 100, topY + 150, 200, 20, Utils.getLocalizedMessage("irc.gui.back"));
		buttonList.add(btnBack);
		
		if(currentProfile != null) {
			loadFromProfile(currentProfile);
		}
	}
	
	private void updateButtonText() {
		btnRelayDeathMessages.displayString = Utils.getLocalizedMessage("irc.gui.config.relayDeathMessages", Utils.getLocalizedMessage((currentProfile.getBoolean(BotProfile.KEY_RELAYDEATHMESSAGES, false) ? "irc.gui.yes" : "irc.gui.no")));
		btnRelayMCJoinLeave.displayString = Utils.getLocalizedMessage("irc.gui.config.relayMinecraftJoins", Utils.getLocalizedMessage((currentProfile.getBoolean(BotProfile.KEY_RELAYMCJOINLEAVE, false) ? "irc.gui.yes" : "irc.gui.no")));
		btnRelayIRCJoinLeave.displayString = Utils.getLocalizedMessage("irc.gui.config.relayIRCJoins", Utils.getLocalizedMessage((currentProfile.getBoolean(BotProfile.KEY_RELAYIRCJOINLEAVE, true) ? "irc.gui.yes" : "irc.gui.no")));
		btnRelayNickChanges.displayString = Utils.getLocalizedMessage("irc.gui.config.relayNickChanges", Utils.getLocalizedMessage((currentProfile.getBoolean(BotProfile.KEY_RELAYNICKCHANGES, true) ? "irc.gui.yes" : "irc.gui.no")));
		btnRelayBroadcasts.displayString = Utils.getLocalizedMessage("irc.gui.config.relayBroadcasts", Utils.getLocalizedMessage((currentProfile.getBoolean(BotProfile.KEY_RELAYBROADCASTS, false) ? "irc.gui.yes" : "irc.gui.no")));
		btnLinkFilter.displayString = Utils.getLocalizedMessage("irc.gui.config.linkFilter", Utils.getLocalizedMessage((currentProfile.getBoolean(BotProfile.KEY_LINKFILTER, false) ? "irc.gui.yes" : "irc.gui.no")));
		btnAutoWho.displayString = Utils.getLocalizedMessage("irc.gui.config.autoWho", Utils.getLocalizedMessage((currentProfile.getBoolean(BotProfile.KEY_AUTOWHO, false) ? "irc.gui.yes" : "irc.gui.no")));
		btnAllowPrivateMessages.displayString = Utils.getLocalizedMessage("irc.gui.config.privateMessages", Utils.getLocalizedMessage((currentProfile.getBoolean(BotProfile.KEY_ALLOWPRIVMSG, true) ? "irc.gui.yes" : "irc.gui.no")));
		btnDisplayFormat.displayString = Utils.getLocalizedMessage("irc.gui.config.messageDisplay", currentProfile.getDisplayFormat());
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnBack) {
			Minecraft.getMinecraft().displayGuiScreen(parentScreen);
			return;
		} else if(button == btnCurrentProfile) {
			enableNameEdit(true);
		} else if(button == btnPrevProfile) {
			if(profileList.isEmpty()) {
				return;
			}
			nextProfile(-1);
		} else if(button == btnNextProfile) {
			if(profileList.isEmpty()) {
				return;
			}
			nextProfile(1);
		} else if(button == btnNewProfile) {
			currentIdx = profileList.size();
			currentProfile = new BotProfile(ConfigurationHandler.getBotProfileDir(), customName);
			ConfigurationHandler.addBotProfile(currentProfile);
			loadFromProfile(currentProfile);
		} else if(button == btnDeleteProfile) {
			if(profileList.isEmpty()) {
				return;
			}
			if(currentProfile.isDefaultProfile()) {
				return;
			}
			ConfigurationHandler.removeBotProfile(currentProfile);
			nextProfile(-1);
			return;
		} else if(currentProfile != null && currentProfile.isDefaultProfile() && button != btnDisplayFormat) {
			currentIdx = profileList.size();
			currentProfile = new BotProfile(currentProfile, currentProfile.getName() + "_copy");
			ConfigurationHandler.addBotProfile(currentProfile);
			loadFromProfile(currentProfile);
			actionPerformed(button);
			return;
		} else if(currentProfile == null) {
			return;
		} else if(button == btnRelayDeathMessages) {
			currentProfile.setBoolean(BotProfile.KEY_RELAYDEATHMESSAGES, !currentProfile.getBoolean(BotProfile.KEY_RELAYDEATHMESSAGES, false));
		} else if(button == btnRelayMCJoinLeave) {
			currentProfile.setBoolean(BotProfile.KEY_RELAYMCJOINLEAVE, !currentProfile.getBoolean(BotProfile.KEY_RELAYMCJOINLEAVE, false));
		} else if(button == btnRelayBroadcasts) {
			currentProfile.setBoolean(BotProfile.KEY_RELAYBROADCASTS, !currentProfile.getBoolean(BotProfile.KEY_RELAYBROADCASTS, false));
		} else if(button == btnRelayIRCJoinLeave) {
			currentProfile.setBoolean(BotProfile.KEY_RELAYIRCJOINLEAVE, !currentProfile.getBoolean(BotProfile.KEY_RELAYIRCJOINLEAVE, true));
		} else if(button == btnRelayNickChanges) {
			currentProfile.setBoolean(BotProfile.KEY_RELAYNICKCHANGES, !currentProfile.getBoolean(BotProfile.KEY_RELAYNICKCHANGES, true));
		} else if(button == btnLinkFilter) {
			currentProfile.setBoolean(BotProfile.KEY_LINKFILTER, !currentProfile.getBoolean(BotProfile.KEY_LINKFILTER, false));
		} else if(button == btnAutoWho) {
			currentProfile.setBoolean(BotProfile.KEY_AUTOWHO, !currentProfile.getBoolean(BotProfile.KEY_AUTOWHO, false));
		} else if(button == btnAllowPrivateMessages) {
			currentProfile.setBoolean(BotProfile.KEY_ALLOWPRIVMSG, !currentProfile.getBoolean(BotProfile.KEY_ALLOWPRIVMSG, false));
		} else if(button == btnDisplayFormat) {
			currentDisplayFormatIdx++;
			if(currentDisplayFormatIdx >= displayFormatList.size()) {
				currentDisplayFormatIdx = 0;
			}
			currentProfile.setDisplayFormat(displayFormatList.get(currentDisplayFormatIdx).getName());
		} else if(button == btnCommands) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiBotCommands(this, currentProfile));
			return;
		}
		if(currentProfile != null) {
			currentProfile.save();
		}
		updateButtonText();
	}
	
	private void nextProfile(int dir) {
		if(profileList.isEmpty()) {
			return;
		}
		currentIdx += dir;
		if(currentIdx < 0) {
			currentIdx = profileList.size() - 1;
		} else if(currentIdx >= profileList.size()) {
			currentIdx = 0;
		}
		currentProfile = profileList.get(currentIdx);
		loadFromProfile(currentProfile);
	}
	
	public void loadFromProfile(BotProfile profile) {
		btnCurrentProfile.displayString = profile.getName();
		btnCurrentProfile.enabled = !profile.isDefaultProfile();
		btnDeleteProfile.enabled = !profile.isDefaultProfile();
		for(int i = 0; i < displayFormatList.size(); i++) {
			if(displayFormatList.get(i).getName().equals(profile.getDisplayFormat())) {
				currentDisplayFormatIdx = i;
				break;
			}
		}
		updateButtonText();
	}
	
	public void enableNameEdit(boolean enabled) {
		if(currentProfile.isDefaultProfile()) {
			return;
		}
		if(enabled && btnCurrentProfile.enabled) {
			txtCurrentProfile.setText(currentProfile.getName());
			txtCurrentProfile.setEnabled(true);
			btnCurrentProfile.displayString = "";
			btnCurrentProfile.enabled = false;
		} else if(!btnCurrentProfile.enabled) {
			ConfigurationHandler.renameBotProfile(currentProfile, txtCurrentProfile.getText());
			btnCurrentProfile.displayString = currentProfile.getName();
			btnCurrentProfile.enabled = true;
			txtCurrentProfile.setEnabled(false);
		}
	}
	
	@Override
	public void updateScreen() {
		txtCurrentProfile.updateCursorCounter();
	}
	
	@Override
	public void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);
		txtCurrentProfile.mouseClicked(par1, par2, par3);
		if(txtCurrentProfile.isEnabled() && !txtCurrentProfile.isFocused()) {
			enableNameEdit(false);
		}
	}
	
	@Override
	public void keyTyped(char unicode, int keyCode) {
		super.keyTyped(unicode, keyCode);
		if(keyCode == Keyboard.KEY_RETURN) {
			enableNameEdit(false);
			return;
		}
		if(txtCurrentProfile.textboxKeyTyped(unicode, keyCode)) {
			return;
		}
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		this.drawCenteredString(fontRendererObj, Utils.getLocalizedMessage("irc.gui.botSettings"), width / 2, height / 2 - 110, Globals.TEXT_COLOR);
		super.drawScreen(par1, par2, par3);
		if(txtCurrentProfile.isEnabled()) {
			txtCurrentProfile.drawTextBox();
		}
	}
}
