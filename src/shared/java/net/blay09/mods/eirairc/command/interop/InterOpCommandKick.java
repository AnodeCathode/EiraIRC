// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.command.interop;

import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.SubCommand;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Utils;
import net.blay09.mods.eirairc.wrapper.CommandSender;
import net.blay09.mods.eirairc.wrapper.SubCommandWrapper;
import net.minecraft.command.CommandException;

import java.util.List;

public class InterOpCommandKick implements SubCommand {

	@Override
	public String getCommandUsage(CommandSender sender) {
		return "eirairc:commands.kick.usage";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public String getCommandName() {
		return "kick";
	}
	
	@Override
	public boolean processCommand(CommandSender sender, IRCContext context, String[] args, boolean serverSide) throws CommandException {
		if(args.length < 2) {
			SubCommandWrapper.throwWrongUsageException(this, sender);
		}
		IRCContext targetChannel = EiraIRCAPI.parseContext(null, args[0], IRCContext.ContextType.IRCChannel);
		if(targetChannel.getContextType() == IRCContext.ContextType.Error) {
			Utils.sendLocalizedMessage(sender, targetChannel.getName(), args[0]);
			return true;
		}
		if(!ConfigHelper.getBotSettings(targetChannel).interOp.get()) {
			Utils.sendLocalizedMessage(sender, "commands.interop.disabled");
			return true;
		}

		IRCContext targetUser = EiraIRCAPI.parseContext(targetChannel, args[1], IRCContext.ContextType.IRCUser);
		if(targetUser.getContextType() == IRCContext.ContextType.Error) {
			Utils.sendLocalizedMessage(sender, targetUser.getName(), args[1]);
			return true;
		}
		String reason = null;
		if(args.length >= 3) {
			reason = args[2];
		}
		targetChannel.getConnection().kick(targetChannel.getName(), targetUser.getName(), reason);
		Utils.sendLocalizedMessage(sender, "commands.kick", targetUser.getName(), targetChannel.getName());
		return true;
	}

	@Override
	public boolean canCommandSenderUseCommand(CommandSender sender) {
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
	public void addTabCompletionOptions(List<String> list, CommandSender sender, String[] args) {
	}

}
