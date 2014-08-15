// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.command.interop;

import java.util.List;

import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.command.SubCommand;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.util.IRCResolver;
import net.blay09.mods.eirairc.util.IRCTargetError;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

public class InterOpCommandMode extends SubCommand {

	@Override
	public String getUsageString(ICommandSender sender) {
		return "irc.commands.interop.mode";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public String getCommandName() {
		return "mode";
	}
	
	@Override
	public boolean processCommand(ICommandSender sender, IRCContext context, String[] args, boolean serverSide) {
		if(args.length < 2) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		IRCContext targetChannel = IRCResolver.resolveTarget(args[0], (short) (IRCResolver.FLAG_CHANNEL + IRCResolver.FLAG_ONCHANNEL));
		if(targetChannel instanceof IRCTargetError) {
			Utils.sendLocalizedMessage(sender, targetChannel.getName(), args[0]);
			return true;
		}
		if(!targetChannel.getConnection().getBot().getProfile(targetChannel).isInterOp()) {
			Utils.sendLocalizedMessage(sender, "irc.interop.disabled");
			return true;
		}
		IRCContext targetUser = null;
		if(args.length >= 3) {
			targetUser = IRCResolver.resolveTarget(args[1], (short) (IRCResolver.FLAG_USER + IRCResolver.FLAG_USERONCHANNEL));
			if(targetUser instanceof IRCTargetError) {
				Utils.sendLocalizedMessage(sender, targetUser.getName(), args[1]);
				return true;
			}
		}
		if(targetUser != null) {
			targetChannel.getConnection().mode(targetChannel.getName(), args[2], targetUser.getName());
			Utils.sendLocalizedMessage(sender, "irc.interop.umode", args[2], targetUser.getName(), targetChannel.getName());
		} else {
			targetChannel.getConnection().mode(targetChannel.getName(), args[1]);
			Utils.sendLocalizedMessage(sender, "irc.interop.mode", args[1], targetChannel.getName());
		}
		return true;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return Utils.isOP(sender);
	}

	@Override
	public boolean hasQuickCommand() {
		return false;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int idx) {
		return false;
	}

	@Override
	public void addTabCompletionOptions(List<String> list, ICommandSender sender, String[] args) {
	}

}
