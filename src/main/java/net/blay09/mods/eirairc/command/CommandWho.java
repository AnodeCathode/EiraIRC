package net.blay09.mods.eirairc.command;

import java.util.List;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.irc.IRCChannel;
import net.blay09.mods.eirairc.irc.IRCConnection;
import net.blay09.mods.eirairc.irc.IRCTarget;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.IRCResolver;
import net.blay09.mods.eirairc.util.IRCTargetError;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

public class CommandWho extends SubCommand {

	@Override
	public String getCommandName() {
		return "who";
	}

	@Override
	public String getUsageString(ICommandSender sender) {
		return "irc.commands.who";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(ICommandSender sender, IRCTarget context, String[] args, boolean serverSide) {
		IRCConnection connection = null;
		if(args.length > 0) {
			connection = IRCResolver.resolveConnection(args[0], IRCResolver.FLAGS_NONE);
			if(connection == null) {
				Utils.sendLocalizedMessage(sender, "irc.target.serverNotFound", args[0]);
				return true;
			}
		} else {
			if(context != null) {
				connection = context.getConnection();
			}
		}
		if(args.length > 0) {
			String channelName = IRCResolver.stripPath(args[0]);
			context = connection.getChannel(channelName);
			if(context == null) {
				Utils.sendLocalizedMessage(sender, "irc.target.channelNotFound", args[0]);
				return true;
			}
		}
		if(context instanceof IRCChannel) {
			Utils.sendUserList(sender, connection, (IRCChannel) context);
		} else if(connection != null) {
			for(IRCChannel channel : connection.getChannels()) {
				Utils.sendUserList(sender, connection, channel);
			}
		} else {
			for(IRCConnection con : EiraIRC.instance.getConnections()) {
				for(IRCChannel channel : con.getChannels()) {
					Utils.sendUserList(sender, connection, channel);
				}
			}
		}
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
