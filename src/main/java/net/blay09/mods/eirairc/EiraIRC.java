// Copyright (c) 2015, Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc;

import net.blay09.mods.eirairc.addon.Compatibility;
import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.IChatHandler;
import net.blay09.mods.eirairc.command.base.CommandIRC;
import net.blay09.mods.eirairc.command.base.CommandServIRC;
import net.blay09.mods.eirairc.command.base.IRCCommandHandler;
import net.blay09.mods.eirairc.command.base.IgnoreCommand;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ChatSessionHandler;
import net.blay09.mods.eirairc.handler.MCEventHandler;
import net.blay09.mods.eirairc.net.EiraNetHandler;
import net.blay09.mods.eirairc.net.NetworkHandler;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = EiraIRC.MOD_ID, acceptableRemoteVersions = "*", guiFactory = "net.blay09.mods.eirairc.client.gui.EiraIRCGuiFactory")
public class EiraIRC {

    public static final String MOD_ID = "eirairc";

    @Instance(MOD_ID)
    public static EiraIRC instance;

    @SidedProxy(serverSide = "net.blay09.mods.eirairc.CommonProxy", clientSide = "net.blay09.mods.eirairc.client.ClientProxy")
    public static CommonProxy proxy;

    private ChatSessionHandler chatSessionHandler;
    private EiraNetHandler netHandler;
    private MCEventHandler mcEventHandler;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigurationHandler.load(event.getModConfigurationDirectory());

        FMLInterModComms.sendRuntimeMessage(this, "VersionChecker", "addVersionCheck", Globals.UPDATE_URL);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        chatSessionHandler = new ChatSessionHandler();
        netHandler = new EiraNetHandler();

        mcEventHandler = new MCEventHandler();

        proxy.init();

        FMLCommonHandler.instance().bus().register(this);
        FMLCommonHandler.instance().bus().register(mcEventHandler);
        MinecraftForge.EVENT_BUS.register(mcEventHandler);
        FMLCommonHandler.instance().bus().register(netHandler);

        NetworkHandler.init();

        EiraIRCAPI.internalSetupAPI(new InternalMethodsImpl());
        EiraIRCAPI.setChatHandler(new IChatHandler() {
            @Override
            public void addChatMessage(IChatComponent component) {
                addChatMessage(null, component);
            }

            @Override
            public void addChatMessage(ICommandSender receiver, IChatComponent component) {
                if (receiver != null) {
                    if (!EiraIRCAPI.hasClientSideInstalled(receiver)) {
                        receiver.addChatMessage(Utils.translateToDefault(component));
                    } else {
                        receiver.addChatMessage(component);
                    }
                } else {
                    Utils.addMessageToChat(component);
                }
            }
        });
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        Compatibility.postInit(event);
        proxy.postInit();
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        registerCommands((CommandHandler) event.getServer().getCommandManager(), true);

        if (!MinecraftServer.getServer().isSinglePlayer()) {
            ConnectionManager.startIRC();
        }
    }

    @EventHandler
    public void serverStop(FMLServerStoppingEvent event) {
        if (!MinecraftServer.getServer().isSinglePlayer()) {
            ConnectionManager.stopIRC();
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(Globals.MOD_ID)) {
            if (event.configID.equals("global")) {
                ConfigurationHandler.lightReload();
                proxy.saveConfig();
            } else if (event.configID.startsWith("server:")) {
                ServerConfig serverConfig = ConfigurationHandler.getOrCreateServerConfig(event.configID.substring(7));
                serverConfig.getTheme().pushDummyConfig();
                serverConfig.getBotSettings().pushDummyConfig();
                serverConfig.getGeneralSettings().pushDummyConfig();
                ConfigurationHandler.saveServers();
            } else if (event.configID.startsWith("channel:")) {
                ChannelConfig channelConfig = ConfigHelper.resolveChannelConfig(event.configID.substring(8));
                if (channelConfig != null) {
                    channelConfig.getTheme().pushDummyConfig();
                    channelConfig.getBotSettings().pushDummyConfig();
                    channelConfig.getGeneralSettings().pushDummyConfig();
                    ConfigurationHandler.saveServers();
                }
            }
        }
    }

    public MCEventHandler getMCEventHandler() {
        return mcEventHandler;
    }

    public ChatSessionHandler getChatSessionHandler() {
        return chatSessionHandler;
    }

    public EiraNetHandler getNetHandler() {
        return netHandler;
    }

    public void registerCommands(CommandHandler handler, boolean serverSide) {
        if (serverSide) {
            handler.registerCommand(new CommandServIRC());
            handler.registerCommand(new IgnoreCommand("irc"));
        } else {
            handler.registerCommand(new CommandIRC());
        }
        IRCCommandHandler.registerCommands();
    }

}
