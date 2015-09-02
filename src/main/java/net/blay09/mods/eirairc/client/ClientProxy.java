// Copyright (c) 2015, Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.client;

import net.blay09.mods.eirairc.CommonProxy;
import net.blay09.mods.eirairc.ConnectionManager;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.addon.DirectUploadHoster;
import net.blay09.mods.eirairc.addon.ImgurHoster;
import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.event.IRCChannelMessageEvent;
import net.blay09.mods.eirairc.client.gui.EiraGui;
import net.blay09.mods.eirairc.client.gui.GuiEiraIRCRedirect;
import net.blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import net.blay09.mods.eirairc.command.base.IRCCommandHandler;
import net.blay09.mods.eirairc.config.*;
import net.blay09.mods.eirairc.util.NotificationType;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ClientProxy extends CommonProxy {

    private static final KeyBinding[] keyBindings = new KeyBinding[]{
            ClientGlobalConfig.keyScreenshotShare,
            ClientGlobalConfig.keyOpenScreenshots,
            ClientGlobalConfig.keyToggleTarget,
            ClientGlobalConfig.keyOpenMenu
    };

    private ClientHandler clientHandler;

    @Override
    public void init() {
        ScreenshotManager.create();

        clientHandler = new ClientHandler();
        MinecraftForge.EVENT_BUS.register(clientHandler);
        FMLCommonHandler.instance().bus().register(clientHandler);

        for (KeyBinding keyBinding : keyBindings) {
            ClientRegistry.registerKeyBinding(keyBinding);
        }

        // Dirty hack to stop toggle target overshadowing player list key when they share the same key code (they do by default)
        KeyBinding keyBindPlayerList = Minecraft.getMinecraft().gameSettings.keyBindPlayerList;
        if (ClientGlobalConfig.keyToggleTarget.getKeyCode() == keyBindPlayerList.getKeyCode()) {
            Minecraft.getMinecraft().gameSettings.keyBindPlayerList = new KeyBinding(keyBindPlayerList.getKeyDescription(), keyBindPlayerList.getKeyCodeDefault(), keyBindPlayerList.getKeyCategory());
            Minecraft.getMinecraft().gameSettings.keyBindPlayerList.setKeyCode(keyBindPlayerList.getKeyCode());
        }

        EiraIRC.instance.registerCommands(ClientCommandHandler.instance, false);
        if (ClientGlobalConfig.registerShortCommands.get()) {
            IRCCommandHandler.registerQuickCommands(ClientCommandHandler.instance);
        }

        EiraGui.init(Minecraft.getMinecraft().getResourceManager());

        try {
            ConfigurationHandler.loadSuggestedChannels(Minecraft.getMinecraft().getResourceManager());
        } catch (IOException ignored) {
        }
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
        switch (type) {
            case FriendJoined:
                config = ClientGlobalConfig.ntfyFriendJoined.get();
                break;
            case PlayerMentioned:
                config = ClientGlobalConfig.ntfyNameMentioned.get();
                break;
            case PrivateMessage:
                config = ClientGlobalConfig.ntfyPrivateMessage.get();
                break;
            default:
        }
        if (config != NotificationStyle.None && config != NotificationStyle.SoundOnly) {
            clientHandler.getNotificationOverlay().showNotification(type, text);
        }
        if (config == NotificationStyle.TextAndSound || config == NotificationStyle.SoundOnly) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation(ClientGlobalConfig.notificationSound.get()), ClientGlobalConfig.notificationSoundPitch.get()));
        }
    }

    @Override
    public String getUsername() {
        return Minecraft.getMinecraft().getSession().getUsername();
    }

    @Override
    public boolean isIngame() {
        return Minecraft.getMinecraft().theWorld != null;
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
    public void handleRedirect(ServerConfig serverConfig) {
        TrustedServer server = ConfigurationHandler.getOrCreateTrustedServer(Utils.getServerAddress());
        if (server.isAllowRedirect()) {
            ConnectionManager.redirectTo(serverConfig, server.isRedirectSolo());
        } else {
            Minecraft.getMinecraft().displayGuiScreen(new GuiEiraIRCRedirect(serverConfig));
        }
    }

    @Override
    public boolean handleConfigCommand(ICommandSender sender, String key, String value) {
        return super.handleConfigCommand(sender, key, value) || ClientGlobalConfig.handleConfigCommand(sender, key, value);
    }

    @Override
    public String handleConfigCommand(ICommandSender sender, String key) {
        String result = super.handleConfigCommand(sender, key);
        if (result == null) {
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
    public boolean checkClientBridge(IRCChannelMessageEvent event) {
        if (event.sender != null && ClientGlobalConfig.clientBridge.get()) {
            for (Object obj : FMLClientHandler.instance().getClientPlayerEntity().sendQueue.func_175106_d()) {
                NetworkPlayerInfo playerInfo = (NetworkPlayerInfo) obj;
                if (event.sender.getName().equalsIgnoreCase(playerInfo.func_178845_a().getName())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void saveConfig() {
        super.saveConfig();
        if (ClientGlobalConfig.thisConfig.hasChanged()) {
            ClientGlobalConfig.thisConfig.save();
        }
    }
}
