// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.command;

import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.blay09.mods.eirairc.config.IgnoreList;
import net.blay09.mods.eirairc.util.Utils;
import net.blay09.mods.eirairc.wrapper.CommandSender;
import net.blay09.mods.eirairc.wrapper.SubCommandWrapper;
import net.minecraft.command.CommandException;

import java.util.List;

public class CommandIgnore implements SubCommand {

	@Override
	public String getCommandName() {
		return "ignore";
	}

	@Override
	public String getCommandUsage(CommandSender sender) {
		return "eirairc:commands.ignore.usage";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean processCommand(CommandSender sender, IRCContext context, String[] args, boolean serverSide) throws CommandException {
		if(args.length < 1) {
			SubCommandWrapper.throwWrongUsageException(this, sender);
		}
		IRCContext target = EiraIRCAPI.parseContext(context, args[0], IRCContext.ContextType.IRCUser);
		if(target.getContextType() == IRCContext.ContextType.Error) {
			Utils.sendLocalizedMessage(sender, target.getName(), args[0]);
			return true;
		}
		IRCUser user = (IRCUser) target;
		if(user.getHostname() == null) {
			Utils.sendLocalizedMessage(sender, "commands.ignore.notKnown", target.getName());
			return true;
		}
		if(IgnoreList.isIgnored(user)) {
			Utils.sendLocalizedMessage(sender, "commands.ignore.alreadyIgnored", target.getName());
			return true;
		}
		IgnoreList.addToIgnoreList(user);
		Utils.sendLocalizedMessage(sender, "commands.ignore.added", target.getName());
		return true;
	}

	@Override
	public boolean canCommandSenderUseCommand(CommandSender sender) {
		return Utils.isOP(sender);
	}

	@Override
	public void addTabCompletionOptions(List<String> list, CommandSender sender, String[] args) {}

	@Override
	public boolean isUsernameIndex(String[] args, int idx) {
		return false;
	}

	@Override
	public boolean hasQuickCommand() {
		return true;
	}

}
