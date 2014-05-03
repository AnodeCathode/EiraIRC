package net.blay09.mods.eirairc.bot;

import net.blay09.mods.eirairc.api.base.IIRCChannel;
import net.blay09.mods.eirairc.api.base.IIRCUser;
import net.blay09.mods.eirairc.api.bot.IIRCBot;
import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.util.Utils;

public class BotCommandAuth implements IBotCommand {

	@Override
	public String getCommandName() {
		return "auth";
	}

	@Override
	public boolean isChannelCommand() {
		return true;
	}

	@Override
	public void processCommand(IIRCBot bot, IIRCChannel channel, IIRCUser user, String[] args) {
		user.whois();
		user.notice(Utils.getLocalizedMessage("irc.bot.auth"));
	}
	
}
