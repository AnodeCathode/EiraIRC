// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.chat;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.event.ClientChatEvent;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.client.gui.GuiEiraIRCMenu;
import net.blay09.mods.eirairc.client.gui.screenshot.GuiImagePreview;
import net.blay09.mods.eirairc.config.ClientGlobalConfig;
import net.blay09.mods.eirairc.handler.ChatSessionHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.MessageFormat;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;

@SideOnly(Side.CLIENT)
public class GuiChatExtended extends GuiChat implements GuiYesNoCallback {

	public static final int COLOR_BACKGROUND = Integer.MIN_VALUE;
	
	private final ChatSessionHandler chatSession;
	private final String defaultInputText;

	public GuiChatExtended() {
		this("");
	}
	
	public GuiChatExtended(String defaultInputText) {
		chatSession = EiraIRC.instance.getChatSessionHandler();
		this.defaultInputText = defaultInputText;
	}

	@Override
	public void initGui() {
		super.initGui();
		inputField.setText(defaultInputText);
	}

	@Override
	protected void keyTyped(char unicode, int keyCode) throws IOException {
		if(keyCode == 28 || keyCode == 156) {
			String s = inputField.getText().trim();
			if(s.length() > 0) {
				if(!FMLCommonHandler.instance().bus().post(new ClientChatEvent(inputField.getText()))) {
					if(s.charAt(0) != '/' || ClientCommandHandler.instance.executeCommand(mc.thePlayer, s) != 1) {
						this.mc.thePlayer.sendChatMessage(s);
					}
				}
				mc.ingameGUI.getChatGUI().addToSentMessages(s);
			}
			mc.displayGuiScreen(null);
			return;
		}
		super.keyTyped(unicode, keyCode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
		if(button == 0 && mc.gameSettings.chatLinks) {
			IChatComponent clickedComponent = mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());
			if(clickedComponent != null) {
				ClickEvent clickEvent = clickedComponent.getChatStyle().getChatClickEvent();
				if(clickEvent != null) {
					// TODO try to replace eirairc:// by EiraClickEvent
					if(clickEvent.getValue().startsWith("eirairc://")) {
						String[] params = clickEvent.getValue().substring(10).split(";");
						if(params.length > 0) {
							if(params[0].equals("screenshot")) {
								try {
									if(ClientGlobalConfig.imageLinkPreview && params[2].length() > 0) {
										mc.displayGuiScreen(new GuiImagePreview(new URL(params[2]), new URL(params[1])));
									} else {
										if(mc.gameSettings.chatLinksPrompt) {
											mc.displayGuiScreen(new GuiConfirmOpenLink(this, params[1], 0, false));
										} else {
											Utils.openWebpage(params[1]);
										}
									}
								} catch (MalformedURLException e) {
									e.printStackTrace();
								}
							}
						}
						return;
					} else {
						// If this is an image link and imageLinkPreview is enabled, open the preview GUI. Otherwise, leave it to the super method.
						if(ClientGlobalConfig.imageLinkPreview && clickEvent.getValue().endsWith(".png") || clickEvent.getValue().endsWith(".jpg")) {
							try {
								mc.displayGuiScreen(new GuiImagePreview(new URL(clickEvent.getValue()), null));
								return;
							} catch (MalformedURLException e) {
								e.printStackTrace();
							}
						}
					}
				} else {
					// Attempt to fix MC-30864 (https://bugs.mojang.com/browse/MC-30864) on the client side as a last resort since Mojang is too busy working on their April Fools jokes
					Matcher urlMatcher = MessageFormat.urlPattern.matcher(clickedComponent.getUnformattedText());
					if(urlMatcher.find()) {
						String url = urlMatcher.group();
						try {
							if(ClientGlobalConfig.imageLinkPreview && (url.endsWith(".png") || url.endsWith(".jpg"))) {
								mc.displayGuiScreen(new GuiImagePreview(new URL(url), null));
							} else {
								if(mc.gameSettings.chatLinksPrompt) {
									mc.displayGuiScreen(new GuiConfirmOpenLink(this, url, 0, false));
								} else {
									Utils.openWebpage(url);
								}
							}
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}
						return;
					}
				}
			}
		}
		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void drawScreen(int i, int j, float k) {
		super.drawScreen(i, j, k);
		if(!ClientGlobalConfig.disableChatToggle && !ClientGlobalConfig.clientBridge) {
			IRCContext target = chatSession.getChatTarget();
			String targetName;
			if(target == null) {
				targetName = "Minecraft";
			} else {
				targetName = target.getName() + " (" + target.getConnection().getHost() + ")";
			}
			String text = Utils.getLocalizedMessage("irc.gui.chatTarget", targetName);
			int rectWidth = Math.max(200, fontRendererObj.getStringWidth(text) + 10);
			drawRect(0, 0, rectWidth, fontRendererObj.FONT_HEIGHT + 6, COLOR_BACKGROUND);
			fontRendererObj.drawString(text, 5, 5, Globals.TEXT_COLOR);
		}
	}
}
