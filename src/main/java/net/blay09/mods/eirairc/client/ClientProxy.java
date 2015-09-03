// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.blay09.mods.eirairc.CommonProxy;
import net.blay09.mods.eirairc.ConnectionManager;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.addon.DirectUploadHoster;
import net.blay09.mods.eirairc.addon.ImgurHoster;
import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.config.IConfigManager;
import net.blay09.mods.eirairc.api.event.IRCChannelMessageEvent;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.client.gui.EiraGui;
import net.blay09.mods.eirairc.client.gui.GuiEiraIRCMenu;
import net.blay09.mods.eirairc.client.gui.GuiEiraIRCRedirect;
import net.blay09.mods.eirairc.client.gui.GuiWelcome;
import net.blay09.mods.eirairc.client.gui.chat.GuiChatExtended;
import net.blay09.mods.eirairc.client.gui.chat.GuiSleepExtended;
import net.blay09.mods.eirairc.client.gui.overlay.OverlayNotification;
import net.blay09.mods.eirairc.client.gui.screenshot.GuiScreenshots;
import net.blay09.mods.eirairc.client.screenshot.Screenshot;
import net.blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import net.blay09.mods.eirairc.command.base.IRCCommandHandler;
import net.blay09.mods.eirairc.config.*;
import net.blay09.mods.eirairc.handler.ChatSessionHandler;
import net.blay09.mods.eirairc.util.NotificationType;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.config.Configuration;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

	private ChatSessionHandler chatSession;
	private int keyChat;
	private int keyCommand;
	private OverlayNotification notificationGUI;
	private int screenshotCheck;
	private int openWelcomeScreen;
	private long lastToggleTarget;
	private boolean wasToggleTargetDown;

	private static final KeyBinding[] keyBindings = new KeyBinding[] {
		ClientGlobalConfig.keyScreenshotShare,
		ClientGlobalConfig.keyOpenScreenshots,
		ClientGlobalConfig.keyToggleTarget,
		ClientGlobalConfig.keyOpenMenu
	};

	@Override
	public void init() {
		chatSession = EiraIRC.instance.getChatSessionHandler();

		Minecraft mc = Minecraft.getMinecraft();

		keyChat = mc.gameSettings.keyBindChat.getKeyCode();
		keyCommand = mc.gameSettings.keyBindCommand.getKeyCode();
		notificationGUI = new OverlayNotification();

		ScreenshotManager.create();

		FMLCommonHandler.instance().bus().register(this);

		for(KeyBinding keyBinding : keyBindings) {
			ClientRegistry.registerKeyBinding(keyBinding);
		}

		// Dirty hack to stop toggle target overshadowing player list key when they share the same key code (they do by default)
		KeyBinding keyBindPlayerList = mc.gameSettings.keyBindPlayerList;
		if(ClientGlobalConfig.keyToggleTarget.getKeyCode() == keyBindPlayerList.getKeyCode()) {
			mc.gameSettings.keyBindPlayerList = new KeyBinding(keyBindPlayerList.getKeyDescription(), keyBindPlayerList.getKeyCodeDefault(), keyBindPlayerList.getKeyCategory());
			mc.gameSettings.keyBindPlayerList.setKeyCode(keyBindPlayerList.getKeyCode());
		}
		
		EiraIRC.instance.registerCommands(ClientCommandHandler.instance, false);
		if(ClientGlobalConfig.registerShortCommands.get()) {
			IRCCommandHandler.registerQuickCommands(ClientCommandHandler.instance);
		}

		EiraGui.init(mc.getResourceManager());
		try {
			ConfigurationHandler.loadSuggestedChannels(mc.getResourceManager());
		} catch (IOException ignored) {}
	}

	@Override
	public void postInit() {
		super.postInit();

		EiraIRCAPI.registerUploadHoster(new DirectUploadHoster());
		EiraIRCAPI.registerUploadHoster(new ImgurHoster());
	}

	@Override
	public void publishNotification(NotificationType type, String text) {
		NotificationStyle config = NotificationStyle.None;
		switch(type) {
		case FriendJoined: config = ClientGlobalConfig.ntfyFriendJoined.get(); break;
		case PlayerMentioned: config = ClientGlobalConfig.ntfyNameMentioned.get(); break;
		case PrivateMessage: config = ClientGlobalConfig.ntfyPrivateMessage.get(); break;
		default:
		}
		if(config != NotificationStyle.None && config != NotificationStyle.SoundOnly) {
			notificationGUI.showNotification(type, text);
		}
		if(config == NotificationStyle.TextAndSound || config == NotificationStyle.SoundOnly) {
			Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation(ClientGlobalConfig.notificationSound.get()), ClientGlobalConfig.notificationSoundPitch.get())); // createPositionedSoundRecord
		}
	}

	@Override
	public String getUsername() {
		return Minecraft.getMinecraft().getSession().getUsername();
	}

	@Override
	public void loadLegacyConfig(File configDir, Configuration legacyConfig) {
		super.loadLegacyConfig(configDir, legacyConfig);
		ClientGlobalConfig.loadLegacy(configDir, legacyConfig);
	}

	@Override
	public void loadConfig(File configDir, boolean reloadFile) {
		super.loadConfig(configDir, reloadFile);
		ClientGlobalConfig.load(configDir, reloadFile);
	}

	@Override
	public boolean handleConfigCommand(ICommandSender sender, String key, String value) {
		return super.handleConfigCommand(sender, key, value) || ClientGlobalConfig.handleConfigCommand(sender, key, value);
	}

	@Override
	public String handleConfigCommand(ICommandSender sender, String key) {
		String result = super.handleConfigCommand(sender, key);
		if(result == null) {
			return ClientGlobalConfig.handleConfigCommand(sender, key);
		} else {
			return result;
		}
	}

	@Override
	public void addConfigOptionsToList(List<String> list, String option, boolean autoCompleteOption) {
		super.addConfigOptionsToList(list, option, autoCompleteOption);
		ClientGlobalConfig.addOptionsToList(list, option, autoCompleteOption);
	}

	@Override
	public void saveConfig() {
		super.saveConfig();
		if (ClientGlobalConfig.thisConfig.hasChanged()) {
			ClientGlobalConfig.thisConfig.save();
		}
	}

	@Override
	public boolean checkClientBridge(IRCChannelMessageEvent event) {
		if(event.sender != null && ClientGlobalConfig.clientBridge.get()) {
			for(Object obj : FMLClientHandler.instance().getClientPlayerEntity().sendQueue.playerInfoList) {
				GuiPlayerInfo playerInfo = (GuiPlayerInfo) obj;
				if(event.sender.getName().equalsIgnoreCase(playerInfo.name)) {
					return true;
				}
			}
		}
		return false;
	}

	@SubscribeEvent
	public void keyInput(InputEvent.KeyInputEvent event) {
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
		if(!ConnectionManager.isIRCRunning()) {
			ConnectionManager.startIRC();
		}
		if(ClientGlobalConfig.showWelcomeScreen.get()) {
			openWelcomeScreen = 20;
		}
	}

	@SubscribeEvent
	public void clientTick(TickEvent.ClientTickEvent event) {
		ConnectionManager.tickConnections();

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
	public void renderTick(TickEvent.RenderTickEvent event) {
		notificationGUI.updateAndRender(event.renderTickTime);
	}

	@Override
	public IConfigManager getClientGlobalConfig() {
		return ClientGlobalConfig.manager;
	}

	@Override
	public void handleRedirect(ServerConfig serverConfig) {
		super.handleRedirect(serverConfig);

		TrustedServer server = ConfigurationHandler.getOrCreateTrustedServer(Utils.getServerAddress());
		if(server.isAllowRedirect()) {
			ConnectionManager.redirectTo(serverConfig, server.isRedirectSolo());
		} else {
			Minecraft.getMinecraft().displayGuiScreen(new GuiEiraIRCRedirect(serverConfig));
		}
	}
}
