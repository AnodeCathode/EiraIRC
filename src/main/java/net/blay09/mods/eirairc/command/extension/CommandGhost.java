package net.blay09.mods.eirairc.command.extension;

import java.util.List;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.command.SubCommand;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.ServiceConfig;
import net.blay09.mods.eirairc.config.ServiceSettings;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.irc.IRCConnection;
import net.blay09.mods.eirairc.irc.IRCTarget;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.IRCResolver;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

public class CommandGhost extends SubCommand {

	@Override
	public String getCommandName() {
		return "ghost";
	}

	@Override
	public String getUsageString(ICommandSender sender) {
		return "irc.commands.ghost";
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
			if(context == null) {
				Utils.sendLocalizedMessage(sender, "irc.specifyServer");
				return true;
			}
			connection = context.getConnection();
		}
		ServerConfig serverConfig = ConfigurationHandler.getServerConfig(connection.getHost());
		ServiceSettings settings = ServiceConfig.getSettings(connection.getHost(), connection.getServerType());
		if(settings.hasGhostCommand()) {
			connection.sendIRC(settings.getGhostCommand(serverConfig.getNickServName(), serverConfig.getNickServPassword()));
		} else {
			Utils.sendLocalizedMessage(sender, "irc.general.notSupported", "GHOST");
		}
		return true;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return Utils.isOP(sender);
	}

	@Override
	public void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args) {
		if(args.length == 0) {
			Utils.addConnectionsToList(list);
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
