// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command;

import java.util.List;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.api.IIRCContext;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.irc.IRCConnection;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.IRCResolver;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

public class CommandNick extends SubCommand {

	@Override
	public String getCommandName() {
		return "nick";
	}

	@Override
	public String getUsageString(ICommandSender sender) {
		return "irc.commands.nick";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(ICommandSender sender, IIRCContext context, String[] args, boolean serverSide) {
		if(args.length < 1) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		if(args.length >= 2) {
			ServerConfig serverConfig = IRCResolver.resolveServerConfig(args[1], IRCResolver.FLAGS_NONE);
			if(serverConfig == null) {
				Utils.sendLocalizedMessage(sender, "irc.target.serverNotFound");
				return true;
			}
			String nick = args[0];
			Utils.sendLocalizedMessage(sender, "irc.basic.changingNick", serverConfig.getHost(), nick);
			serverConfig.setNick(nick);
			IIRCConnection connection = EiraIRC.instance.getConnection(serverConfig.getHost());
			if(connection != null) {
				connection.nick(nick);
			}
		} else {
			String nick = args[0];
			if(context == null) {
				Utils.sendLocalizedMessage(sender, "irc.basic.changingNick", "Global", nick);
				GlobalConfig.nick = nick;
				for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
					if(serverConfig.getHost().equals(Globals.TWITCH_SERVER)) {
						continue;
					}
					if(serverConfig.getNick() == null || serverConfig.getNick().isEmpty()) {
						IIRCConnection connection = EiraIRC.instance.getConnection(serverConfig.getHost());
						if(connection != null) {
							connection.nick(ConfigHelper.formatNick(nick));
						}
					}
				}
			} else {
				IIRCConnection connection = context.getConnection();
				if(connection.getHost().equals(Globals.TWITCH_SERVER)) {
					return true;
				}
				connection.nick(ConfigHelper.formatNick(nick));
				ServerConfig serverConfig = ConfigHelper.getServerConfig(connection);
				serverConfig.setNick(nick);
			}
		}
		ConfigurationHandler.save();
		return true;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return Utils.isOP(sender);
	}

	@Override
	public void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args) {
	}

	@Override
	public boolean isUsernameIndex(String[] args, int idx) {
		return false;
	}

	@Override
	public boolean hasQuickCommand() {
		return true;
	}

}
