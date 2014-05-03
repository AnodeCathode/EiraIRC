// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.bot;

import java.util.List;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCUser;
import net.blay09.mods.eirairc.api.bot.IIRCBot;
import net.blay09.mods.eirairc.api.bot.IBotCommand;
import net.blay09.mods.eirairc.api.bot.IBotProfile;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.NotificationType;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatAllowedCharacters;

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
	public void processCommand(IIRCBot bot, IIRCChannel channel, IIRCUser user, String[] args) {
		if(!bot.getBoolean(channel, IBotProfile.KEY_ALLOWPRIVMSG, false)) {
			user.notice(Utils.getLocalizedMessage("irc.msg.disabled"));
		}
		String playerName = args[0];
		EntityPlayer entityPlayer = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(playerName);
		if(entityPlayer == null) {
			List<EntityPlayer> playerEntityList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			for(EntityPlayer entity : playerEntityList) {
				if(Utils.getAliasForPlayer(entity, false).equals(playerName) || Utils.getAliasForPlayer(entity, true).equals(playerName)) {
					entityPlayer = entity;
				}
			}
			if(entityPlayer == null) {
				user.notice(Utils.getLocalizedMessage("irc.general.noSuchPlayer"));
				return;
			}
		}
		String message = Utils.joinArgs(args, 1);
		if(GlobalConfig.enableLinkFilter) {
			message = Utils.filterLinks(message);
		}
		message = Utils.filterCodes(message);
		message = ChatAllowedCharacters.filerAllowedCharacters(message);
		String mcMessage = Utils.formatMessageNew(ConfigHelper.getDisplayFormatConfig().mcPrivateMessage, bot.getConnection(), null, user, message);
		String notifyMsg = mcMessage;
		if(notifyMsg.length() > 42) {
			notifyMsg = notifyMsg.substring(0, 42) + "...";
		}
		EiraIRC.proxy.sendNotification((EntityPlayerMP) entityPlayer, NotificationType.PrivateMessage, notifyMsg);
		entityPlayer.addChatMessage(Utils.getUnlocalizedChatMessage(mcMessage));
		user.notice(Utils.getLocalizedMessage("irc.bot.msgSent", playerName, message));
	}
	
}
