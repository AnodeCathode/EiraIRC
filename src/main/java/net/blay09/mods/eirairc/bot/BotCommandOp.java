// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.bot;

import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCUser;
import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.api.bot.IBotProfile;
import net.blay09.mods.eirairc.api.bot.IIRCBot;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.server.MinecraftServer;

public class BotCommandOp implements IBotCommand {

	@Override
	public String getCommandName() {
		return "op";
	}

	@Override
	public boolean isChannelCommand() {
		return true;
	}

	@Override
	public void processCommand(IIRCBot bot, IIRCChannel channel, IIRCUser user, String[] args) {
		IBotProfile botProfile = bot.getMainProfile();
		if(!botProfile.isInterOp(user.getAuthLogin()) && !bot.getProfile(channel).isInterOp(user.getAuthLogin())) {
			user.notice(Utils.getLocalizedMessage("irc.bot.noPermission"));
			return;
		}
		String message = Utils.joinArgs(args, 0).trim();
		if(message.isEmpty()) {
			user.notice("Usage: !op <command>");
			return;
		}
		String[] commandBlacklist = bot.getProfile(channel).getInterOpBlacklist();
		for(int i = 0; i < commandBlacklist.length; i++) {
			if(message.contains(commandBlacklist[i])) {
				user.notice(Utils.getLocalizedMessage("irc.bot.interOpBlacklist"));
				return;
			}
		}
		bot.resetLog();
		bot.setOpEnabled(true);
		MinecraftServer.getServer().getCommandManager().executeCommand(bot, message);
		bot.setOpEnabled(false);
		user.notice("> " + bot.getLogContents());
	}
	
}
