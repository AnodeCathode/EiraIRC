// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command;

import java.util.List;

import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.api.bot.BotProfile;
import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.blay09.mods.eirairc.config2.ChannelConfig;
import net.blay09.mods.eirairc.config2.ServerConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.irc.IRCUserImpl;
import net.blay09.mods.eirairc.util.IRCResolver;
import net.blay09.mods.eirairc.util.IRCTargetError;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

public class CommandMessage extends SubCommand {

	@Override
	public String getCommandName() {
		return "msg";
	}

	@Override
	public String getUsageString(ICommandSender sender) {
		return "irc.commands.msg";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) {
		if(args.length < 2) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		short flags = IRCResolver.FLAG_CHANNEL + IRCResolver.FLAG_ONCHANNEL + IRCResolver.FLAG_USER;
		if(serverSide && !Utils.isOP(sender)) {
			flags += IRCResolver.FLAG_USERONCHANNEL;
		}
		IRCContext target = IRCResolver.resolveTarget(args[0], flags);
		if(target instanceof IRCTargetError) {
			Utils.sendLocalizedMessage(sender, target.getName(), args[0]);
			return true;
		} else if(target instanceof IRCUserImpl) {
			IRCBot bot = target.getConnection().getBot();
			if(bot.getBoolean(target, BotProfile.KEY_ALLOWPRIVMSG, true)) {
				Utils.sendLocalizedMessage(sender, "irc.msg.disabled");
				return true;
			}
		}
		String message = Utils.joinStrings(args, " ", 1).trim();
		if(message.isEmpty()) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		String ircMessage = message;
		if(serverSide) {
			ircMessage = "<" + Utils.getNickIRC((EntityPlayer) sender) + "> " + ircMessage;
		}
		target.message(ircMessage);
		String mcMessage = "[-> " + target.getName() + "] <" + Utils.getNickGame((EntityPlayer) sender) + "> " + message;
		sender.addChatMessage(new ChatComponentText(mcMessage));
		return true;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args) {
		if(args.length == 0) {
			for(ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
				for(ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
					list.add(channelConfig.getName());
				}
			}
		}
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
