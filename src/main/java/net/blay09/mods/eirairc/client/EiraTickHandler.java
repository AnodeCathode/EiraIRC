// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client;

import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.handler.ChatSessionHandler;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.client.gui.GuiEiraIRCMenu;
import net.blay09.mods.eirairc.client.gui.GuiWelcome;
import net.blay09.mods.eirairc.client.gui.chat.GuiChatExtended;
import net.blay09.mods.eirairc.client.gui.chat.GuiEiraChat;
import net.blay09.mods.eirairc.client.gui.chat.GuiEiraChatInput;
import net.blay09.mods.eirairc.client.gui.screenshot.GuiScreenshots;
import net.blay09.mods.eirairc.client.screenshot.Screenshot;
import net.blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import net.blay09.mods.eirairc.config.ClientGlobalConfig;
import net.blay09.mods.eirairc.config.ScreenshotAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

public class EiraTickHandler {

	private final GuiEiraChat eiraChat;
	private final ChatSessionHandler chatSession;
	private int screenshotCheck;
	private boolean[] keyState = new boolean[10];
	private final int keyChat;
	private final int keyCommand;
	private int openWelcomeScreen;
	private long lastToggleTarget;

	public EiraTickHandler(GuiEiraChat eiraChat) {
		this.eiraChat = eiraChat;
		this.chatSession = EiraIRC.instance.getChatSessionHandler();
		keyChat = Minecraft.getMinecraft().gameSettings.keyBindChat.getKeyCode();
		keyCommand = Minecraft.getMinecraft().gameSettings.keyBindCommand.getKeyCode();
	}

	private boolean isKeyPressed(KeyBinding keyBinding, int keyIdx) {
		if(keyBinding.getKeyCode() <= 0) {
			return false;
		}
		if(Keyboard.isKeyDown(keyBinding.getKeyCode())) {
			if(!keyState[keyIdx]) {
				keyState[keyIdx] = true;
				return true;
			}
		} else {
			keyState[keyIdx] = false;
		}
		return false;
	}

	@SubscribeEvent
	public void keyInput(KeyInputEvent event) {
		if(Keyboard.getEventKeyState()) {
			int keyCode = Keyboard.getEventKey();
			if(keyCode == ClientGlobalConfig.keyToggleTarget.getKeyCode() && !ClientGlobalConfig.disableChatToggle && !ClientGlobalConfig.clientBridge) {
				if(Minecraft.getMinecraft().currentScreen instanceof GuiChat) {
					if(Keyboard.isRepeatEvent()) {
						if(System.currentTimeMillis() - lastToggleTarget >= 1000) {
							chatSession.setChatTarget(null);
							if(ClientGlobalConfig.vanillaChat) {
								Utils.addMessageToChat(new ChatComponentTranslation("eirairc:irc.general.chattingTo", "Minecraft"));
							}
						}
					} else {
						boolean users = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
						IRCContext newTarget = chatSession.getNextTarget(users);
						if(!users) {
							lastToggleTarget = System.currentTimeMillis();
						}
						if(!users || newTarget != null) {
							chatSession.setChatTarget(newTarget);
							if(ClientGlobalConfig.vanillaChat) {
								Utils.addMessageToChat(new ChatComponentTranslation("eirairc:irc.general.chattingTo", newTarget == null ? "Minecraft" : newTarget.getName()));
							}
						}
					}
				}
			} else if(keyCode == ClientGlobalConfig.keyOpenMenu.getKeyCode()) {
				if(Minecraft.getMinecraft().currentScreen == null) {
					Minecraft.getMinecraft().displayGuiScreen(new GuiEiraIRCMenu());
				}
			} else if(keyCode == ClientGlobalConfig.keyOpenScreenshots.getKeyCode()) {
				if (Minecraft.getMinecraft().currentScreen == null) {
					Minecraft.getMinecraft().displayGuiScreen(new GuiScreenshots(null));
				}
			} else if(keyCode == ClientGlobalConfig.keyScreenshotShare.getKeyCode()) {
				Screenshot screenshot = ScreenshotManager.getInstance().takeScreenshot();
				if(screenshot != null) {
					ScreenshotManager.getInstance().uploadScreenshot(screenshot, ScreenshotAction.UploadShare);
				}
			} else {
				if(!ClientGlobalConfig.vanillaChat) {
					GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
					if(currentScreen == null || currentScreen.getClass() == GuiChat.class) {
						if(Keyboard.getEventKey() == keyChat) {
							Minecraft.getMinecraft().gameSettings.keyBindChat.isPressed();
							Minecraft.getMinecraft().displayGuiScreen(new GuiChatExtended());
						} else if(Keyboard.getEventKey() == keyCommand) {
							Minecraft.getMinecraft().gameSettings.keyBindCommand.isPressed();
							Minecraft.getMinecraft().displayGuiScreen(new GuiChatExtended("/"));
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void worldJoined(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		if(!EiraIRC.instance.getConnectionManager().isIRCRunning()) {
			EiraIRC.instance.getConnectionManager().startIRC();
		}
		if(ClientGlobalConfig.showWelcomeScreen) {
			openWelcomeScreen = 20;
		}
	}
	
	@SubscribeEvent
	public void clientTick(ClientTickEvent event) {
		if(Minecraft.getMinecraft().currentScreen == null && openWelcomeScreen > 0) {
			openWelcomeScreen--;
			if(openWelcomeScreen <= 0) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiWelcome());
			}
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_F2)) {
			screenshotCheck = 10;
		} else if(screenshotCheck > 0) {
			screenshotCheck--;
			if(screenshotCheck == 0) {
				ScreenshotManager.getInstance().findNewScreenshots(true);
			}
		}
		ScreenshotManager.getInstance().clientTick(event);
	}
	
	@SubscribeEvent
	public void renderTick(RenderTickEvent event) {
		EiraIRC.proxy.renderTick(event.renderTickTime);
	}

}
