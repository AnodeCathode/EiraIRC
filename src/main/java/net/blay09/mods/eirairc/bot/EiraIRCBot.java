// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.bot;

import java.util.HashMap;
import java.util.Map;

import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.api.IIRCContext;
import net.blay09.mods.eirairc.api.IIRCUser;
import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.api.bot.IBotProfile;
import net.blay09.mods.eirairc.api.bot.IIRCBot;
import net.blay09.mods.eirairc.config.BotProfile;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.irc.IRCConnection;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class EiraIRCBot implements IIRCBot {

	private final IRCConnection connection;
	private final Map<String, BotProfile> profiles = new HashMap<String, BotProfile>();
	private final StringBuffer logBuffer = new StringBuffer();
	private BotProfile mainProfile;
	private boolean opEnabled;
	
	public EiraIRCBot(IRCConnection connection) {
		this.connection = connection;
		updateProfiles();
	}
	
	public BotProfile getProfile(String channelName) {
		BotProfile profile = profiles.get(channelName.toLowerCase());
		if(profile == null) {
			return mainProfile;
		}
		return profile;
	}
	
	@Override
	public IBotProfile getProfile(IIRCContext channel) {
		if(channel == null) {
			return mainProfile;
		}
		return getProfile(channel.getName());
	}
	
	@Override
	public IBotProfile getMainProfile() {
		return mainProfile;
	}
	
	@Override
	public String getCommandSenderName() {
		return "EiraIRC Bot (" + connection.getHost() + ")";
	}

	@Override
	public IChatComponent func_145748_c_() {
		return new ChatComponentText(this.getCommandSenderName());
	}

	@Override
	public void addChatMessage(IChatComponent chatComponent) {
		logBuffer.append(chatComponent.getUnformattedText());
	}

	@Override
	public boolean canCommandSenderUseCommand(int level, String commandName) {
		return opEnabled;
	}

	@Override
	public ChunkCoordinates getPlayerCoordinates() {
		return new ChunkCoordinates(0, 0, 0);
	}

	@Override
	public World getEntityWorld() {
		return MinecraftServer.getServer().getEntityWorld();
	}

	@Override
	public IIRCConnection getConnection() {
		return connection;
	}

	@Override
	public void resetLog() {
		logBuffer.setLength(0);
	}

	@Override
	public String getLogContents() {
		return logBuffer.toString();
	}

	public void setOpEnabled(boolean opEnabled) {
		this.opEnabled = opEnabled;
	}
	
	@Override
	public boolean processCommand(IIRCChannel channel, IIRCUser sender, String message) {
		String[] args = message.split(" ");
		IBotCommand botCommand = null;
		IBotProfile botProfile = getProfile(channel);
		if(botProfile != null) {
			botCommand = botProfile.getCommand(args[0]);
			if(botCommand == null) {
				botProfile = mainProfile;
			}
		} else {
			botProfile = mainProfile;
		}
		if(botCommand == null) {
			botCommand = botProfile.getCommand(args[0]);
			if(botCommand == null) {
				return false;
			}
		}
		if(channel != null && !botCommand.isChannelCommand()) {
			return false;
		}
		String[] shiftedArgs = Utils.shiftArgs(args, 1);
		botCommand.processCommand(this, channel, sender, shiftedArgs);
		return true;
	}

	@Override
	public boolean getBoolean(IIRCContext context, String key, boolean defaultVal) {
		return mainProfile.getBoolean(key, defaultVal) && (context != null ? getProfile(context).getBoolean(key, defaultVal) : true);
	}

	@Override
	public boolean isMuted(IIRCContext context) {
		return mainProfile.isMuted() || (context != null ? getProfile(context).isMuted() : false);
	}

	@Override
	public boolean isReadOnly(IIRCContext context) {
		return mainProfile.isReadOnly() || (context != null ? getProfile(context).isReadOnly() : false);
	}

	@Override
	public boolean isServerSide() {
		return Utils.isServerSide();
	}

	@Override
	public String getDisplayFormat(IIRCContext context) {
		return (context != null ? getProfile(context).getDisplayFormat() : mainProfile.getDisplayFormat());
	}

	public void updateProfiles() {
		ServerConfig serverConfig = ConfigHelper.getServerConfig(connection);
		mainProfile = ConfigurationHandler.getBotProfile(serverConfig.getBotProfile());
		profiles.clear();
		for(ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
			if(!channelConfig.getBotProfile().equals(mainProfile.getName()) && !channelConfig.getBotProfile().equals(IBotProfile.INHERIT)) {
				profiles.put(channelConfig.getName().toLowerCase(), ConfigurationHandler.getBotProfile(channelConfig.getBotProfile()));
			}
		}
	}

}
