// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.client.gui.GuiEiraIRCMenu;
import net.blay09.mods.eirairc.client.gui.GuiWelcome;
import net.blay09.mods.eirairc.client.gui.chat.GuiChatExtended;
import net.blay09.mods.eirairc.client.gui.chat.GuiSleepExtended;
import net.blay09.mods.eirairc.client.gui.screenshot.GuiScreenshots;
import net.blay09.mods.eirairc.client.screenshot.Screenshot;
import net.blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import net.blay09.mods.eirairc.config.ClientGlobalConfig;
import net.blay09.mods.eirairc.config.ScreenshotAction;
import net.blay09.mods.eirairc.handler.ChatSessionHandler;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.client.event.GuiScreenEvent;
import org.lwjgl.input.Keyboard;

@SuppressWarnings("unused")
public class EiraTickHandler {

	private final ChatSessionHandler chatSession;
	private final int keyChat;
	private final int keyCommand;
	private int screenshotCheck;
	private int openWelcomeScreen;
	private long lastToggleTarget;
	private boolean wasToggleTargetDown;

	public EiraTickHandler() {
		this.chatSession = EiraIRC.instance.getChatSessionHandler();
		keyChat = Minecraft.getMinecraft().gameSettings.keyBindChat.getKeyCode();
		keyCommand = Minecraft.getMinecraft().gameSettings.keyBindCommand.getKeyCode();
	}

	@SubscribeEvent
	public void keyInput(KeyInputEvent event) {
		if(Keyboard.getEventKeyState()) {
			int keyCode = Keyboard.getEventKey();
			if(ClientGlobalConfig.keyOpenMenu.getKeyCode() != 0 && keyCode == ClientGlobalConfig.keyOpenMenu.getKeyCode()) {
				if(Minecraft.getMinecraft().currentScreen == null) {
					Minecraft.getMinecraft().displayGuiScreen(new GuiEiraIRCMenu());
				}
			} else if(ClientGlobalConfig.keyOpenScreenshots.getKeyCode() != 0 && keyCode == ClientGlobalConfig.keyOpenScreenshots.getKeyCode()) {
				if (Minecraft.getMinecraft().currentScreen == null) {
					Minecraft.getMinecraft().displayGuiScreen(new GuiScreenshots(null));
				}
			} else if(ClientGlobalConfig.keyScreenshotShare.getKeyCode() != 0 && keyCode == ClientGlobalConfig.keyScreenshotShare.getKeyCode()) {
				Screenshot screenshot = ScreenshotManager.getInstance().takeScreenshot();
				if(screenshot != null) {
					ScreenshotManager.getInstance().uploadScreenshot(screenshot, ScreenshotAction.UploadShare);
				}
			} else {
				if(!ClientGlobalConfig.chatNoOverride.get()) {
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
		if(ClientGlobalConfig.showWelcomeScreen.get()) {
			openWelcomeScreen = 20;
		}
	}
	
	@SubscribeEvent
	public void clientTick(ClientTickEvent event) {
		GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
		if(currentScreen == null && openWelcomeScreen > 0) {
			openWelcomeScreen--;
			if(openWelcomeScreen <= 0) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiWelcome());
			}
		}

		if(currentScreen instanceof GuiChat && !ClientGlobalConfig.disableChatToggle.get() && !ClientGlobalConfig.clientBridge.get()) {
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Keyboard.isKeyDown(ClientGlobalConfig.keyToggleTarget.getKeyCode())) {
				if(!wasToggleTargetDown) {
					boolean users = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
					IRCContext newTarget = chatSession.getNextTarget(users);
					if(!users) {
						lastToggleTarget = System.currentTimeMillis();
					}
					if(!users || newTarget != null) {
						chatSession.setChatTarget(newTarget);
						if(ClientGlobalConfig.chatNoOverride.get()) {
							Utils.addMessageToChat(new ChatComponentTranslation("eirairc:general.chattingTo", newTarget == null ? "Minecraft" : newTarget.getName()));
						}
					}
					wasToggleTargetDown = true;
				} else {
					if(System.currentTimeMillis() - lastToggleTarget >= 1000) {
						chatSession.setChatTarget(null);
						if(ClientGlobalConfig.chatNoOverride.get()) {
							Utils.addMessageToChat(new ChatComponentTranslation("eirairc:general.chattingTo", "Minecraft"));
						}
						lastToggleTarget = System.currentTimeMillis();
					}
				}
			} else {
				wasToggleTargetDown = false;
			}
		}

		if(currentScreen != null && currentScreen.getClass() == GuiSleepMP.class) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiSleepExtended());
		}

		if(Minecraft.getMinecraft().gameSettings.keyBindScreenshot.getKeyCode() > 0 && Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindScreenshot.getKeyCode())) {
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
