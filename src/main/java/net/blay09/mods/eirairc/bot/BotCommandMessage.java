// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.bot;

import java.util.List;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCUser;
import net.blay09.mods.eirairc.api.bot.BotProfile;
import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.blay09.mods.eirairc.config.DisplayConfig;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.MessageFormat;
import net.blay09.mods.eirairc.util.NotificationType;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;

public class BotCommandMessage implements IBotCommand {

	@Override
	public String getCommandName() {
		return "msg";
	}

	@Override
	public boolean isChannelCommand() {
		return false;
	}

	@Override
	public void processCommand(IRCBot bot, IRCChannel channel, IRCUser user, String[] args) {
		if(!bot.getBoolean(channel, BotProfile.KEY_ALLOWPRIVMSG, false)) {
			user.notice(Utils.getLocalizedMessage("irc.msg.disabled"));
		}
		String playerName = args[0];
		EntityPlayer entityPlayer = MinecraftServer.getServer().getConfigurationManager().func_152612_a(playerName); // getPlayerForUsername
		if(entityPlayer == null) {
			List<EntityPlayer> playerEntityList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			for(EntityPlayer entity : playerEntityList) {
				if(Utils.getNickGame(entity).equals(playerName) || Utils.getNickIRC(entity).equals(playerName)) {
					entityPlayer = entity;
				}
			}
			if(entityPlayer == null) {
				user.notice(Utils.getLocalizedMessage("irc.general.noSuchPlayer"));
				return;
			}
		}
		String message = Utils.joinStrings(args, " ", 1);
		if(bot.getBoolean(user, BotProfile.KEY_LINKFILTER, false)) {
			message = MessageFormat.filterLinks(message);
		}
		IChatComponent chatComponent = MessageFormat.formatChatComponent(ConfigHelper.getDisplayFormat(bot.getDisplayFormat(user)).mcPrivateMessage, bot.getConnection(), null, user, message, MessageFormat.Target.Minecraft, MessageFormat.Mode.Message);
		String notifyMsg = chatComponent.getUnformattedText();
		if(notifyMsg.length() > 42) {
			notifyMsg = notifyMsg.substring(0, 42) + "...";
		}
		EiraIRC.proxy.sendNotification((EntityPlayerMP) entityPlayer, NotificationType.PrivateMessage, notifyMsg);
		entityPlayer.addChatMessage(chatComponent);
		user.notice(Utils.getLocalizedMessage("irc.bot.msgSent", playerName, message));
	}

	@Override
	public String getCommandDescription() {
		return "Send a private message to an online player.";
	}
	
}
