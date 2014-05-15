// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.handler;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.api.IIRCUser;
import net.blay09.mods.eirairc.api.bot.IBotProfile;
import net.blay09.mods.eirairc.api.event.IRCChannelChatEvent;
import net.blay09.mods.eirairc.api.event.IRCChannelTopicEvent;
import net.blay09.mods.eirairc.api.event.IRCPrivateChatEvent;
import net.blay09.mods.eirairc.api.event.IRCUserJoinEvent;
import net.blay09.mods.eirairc.api.event.IRCUserLeaveEvent;
import net.blay09.mods.eirairc.api.event.IRCUserNickChangeEvent;
import net.blay09.mods.eirairc.api.event.IRCUserQuitEvent;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.irc.IRCChannel;
import net.blay09.mods.eirairc.irc.IRCConnection;
import net.blay09.mods.eirairc.irc.IRCUser;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.NotificationType;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class IRCEventHandler {
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onNickChange(IRCUserNickChangeEvent event) {
		if(event.bot.isMuted(null)) {
			return;
		}
		if(event.bot.getBoolean(null, IBotProfile.KEY_RELAYNICKCHANGES, true)) {
			String mcMessage = Utils.getLocalizedMessage("irc.display.irc.nickChange", event.connection.getHost(), event.oldNick, event.newNick);
			Utils.addMessageToChat(mcMessage);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onUserJoin(IRCUserJoinEvent event) {
		if(event.bot.isMuted(event.channel)) {
			return;
		}
		if(event.bot.getBoolean(event.channel, IBotProfile.KEY_RELAYIRCJOINLEAVE, true)) {
			String mcMessage = Utils.getLocalizedMessage("irc.display.irc.joinMsg", event.channel.getName(), event.user.getName());
			Utils.addMessageToChat(mcMessage);
		}
		if(event.bot.getBoolean(event.channel, IBotProfile.KEY_AUTOPLAYERS, false)) {
			Utils.sendPlayerList(event.user);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onUserLeave(IRCUserLeaveEvent event) {
		if(event.bot.isMuted(event.channel)) {
			return;
		}
		if(event.bot.getBoolean(event.channel, IBotProfile.KEY_RELAYIRCJOINLEAVE, true)) {
			String mcMessage = Utils.getLocalizedMessage("irc.display.irc.partMsg", event.channel.getName(), event.user.getName());
			Utils.addMessageToChat(mcMessage);
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onUserQuit(IRCUserQuitEvent event) {
		if(event.bot.isMuted(null)) {
			return;
		}
		if(event.bot.getBoolean(null, IBotProfile.KEY_RELAYIRCJOINLEAVE, true)) {
			String mcMessage = Utils.getLocalizedMessage("irc.display.irc.quitMsg", event.connection.getHost(), event.user.getName(), event.message);
			Utils.addMessageToChat(mcMessage);
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPrivateChat(IRCPrivateChatEvent event) {
		if(event.bot.processCommand(null, event.sender, event.message)) {
			return;
		} else {
			if(event.bot.isServerSide()) {
				event.sender.notice(Utils.getLocalizedMessage("irc.bot.unknownCommand"));
				return;
			}
		}
		if(event.bot.isMuted(event.sender)) {
			return;
		}
		if(!event.bot.getBoolean(event.sender, "allowPrivateMessages", true)) {
			event.sender.notice(Utils.getLocalizedMessage("irc.msg.disabled"));
			return;
		}
		String message = event.message;
		if(event.bot.getBoolean(event.sender, IBotProfile.KEY_LINKFILTER, false)) {
			message = Utils.filterLinks(message);
		}
		message = Utils.filterCodes(message);
		message = ChatAllowedCharacters.filerAllowedCharacters(message);
		String mcMessage = null;
		if(event.isEmote) {
			mcMessage = ConfigHelper.getDisplayFormat(event.bot.getDisplayFormat(event.sender)).mcPrivateEmote;
		} else {
			mcMessage = ConfigHelper.getDisplayFormat(event.bot.getDisplayFormat(event.sender)).mcPrivateMessage;
		}
		mcMessage = Utils.formatMessage(mcMessage, event.connection, null, event.sender, message, !event.isEmote);
		String notifyMsg = mcMessage;
		if(notifyMsg.length() > 42) {
			notifyMsg = notifyMsg.substring(0, 42) + "...";
		}
		EiraIRC.proxy.publishNotification(NotificationType.PrivateMessage, notifyMsg);
		EiraIRC.instance.getChatSessionHandler().addTargetUser(event.sender);
		IChatComponent chatComponent = new ChatComponentText(mcMessage);
		String emoteColor = ConfigHelper.getEmoteColor(event.sender);
		if(event.isEmote && emoteColor != null) {
			chatComponent.getChatStyle().setColor(Utils.getColorFormatting(emoteColor));
		}
		Utils.addMessageToChat(mcMessage);
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onChannelChat(IRCChannelChatEvent event) {
		if(event.message.startsWith("!") && event.bot.processCommand(event.channel, event.sender, event.message.substring(1))) {
			return;
		}
		if(event.bot.isMuted(event.channel)) {
			return;
		}
		String message = event.message;
		if(event.bot.getBoolean(event.sender, IBotProfile.KEY_LINKFILTER, false)) {
			message = Utils.filterLinks(message);
		}
		message = Utils.filterCodes(message);
		message = ChatAllowedCharacters.filerAllowedCharacters(message);
		String emoteColor = ConfigHelper.getEmoteColor(event.channel);
		String mcMessage = null;
		if(event.isEmote) {
			mcMessage = ConfigHelper.getDisplayFormat(event.bot.getDisplayFormat(event.channel)).mcChannelEmote;
		} else {
			mcMessage = ConfigHelper.getDisplayFormat(event.bot.getDisplayFormat(event.channel)).mcChannelMessage;
		}
		mcMessage = Utils.formatMessage(mcMessage, event.connection, event.channel, event.sender, message, !event.isEmote);
		IChatComponent chatComponent = new ChatComponentText(mcMessage);
		if(event.isEmote && emoteColor != null) {
			chatComponent.getChatStyle().setColor(Utils.getColorFormatting(emoteColor));
		}
		Utils.addMessageToChat(chatComponent);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onTopicChange(IRCChannelTopicEvent event) {
		if(event.bot.isMuted(event.channel)) {
			return;
		}
		if(event.user == null) {
			String mcMessage = Utils.getLocalizedMessage("irc.display.irc.topic", event.channel.getName(), event.channel.getTopic());
			Utils.addMessageToChat(mcMessage);
		} else {
			String mcMessage = Utils.getLocalizedMessage("irc.display.irc.topicChange", event.user.getName(), event.channel.getName(), event.channel.getTopic());
			Utils.addMessageToChat(mcMessage);
		}
	}

}
