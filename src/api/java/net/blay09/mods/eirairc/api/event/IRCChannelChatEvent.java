// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.api.event;


import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.api.irc.IRCMessage;
import net.blay09.mods.eirairc.api.irc.IRCUser;

/**
 * This event is published on the MinecraftForge.EVENTBUS bus whenever a message was sent to an IRC channel EiraIRC is in.
 * If this event is cancelled, EiraIRC will not post the message in chat.
 */
public class IRCChannelChatEvent extends IRCChannelChatOrCTCPEvent {

	/**
	 * the channel this IRC message came from
	 *
	 * DEPRECATED: use IRCChannelChatOrCTCPEvent's channel instead
	 */
	@Deprecated
	public final IRCChannel channel;

	/**
	 * the user that sent this IRC message
	 *
	 * DEPRECATED: use IRCChatOrCTCPEvent's sender instead
	 */
	@Deprecated
	public final IRCUser sender;

	/**
	 * the raw IRC message that was sent
	 *
	 * DEPRECATED: use IRCMessageEvent's rawMessage instead
	 */
	@Deprecated
	public IRCMessage rawMessage;

	/**
	 * the message that was sent
	 *
	 * DEPRECATED: use IRCChatOrCTCPEvent's message instead
	 */
	@Deprecated
	public final String message;

	/**
	 * true, if this message is an emote
	 */
	public final boolean isEmote;

	/**
	 * true, fi this message was sent as a NOTICE
	 *
	 * DEPRECATED: use IRCChatOrCTCPEvent's isNotice instead
	 */
	@Deprecated
	public final boolean isNotice;

	/**
	 * INTERNAL EVENT. YOU SHOULD NOT POST THIS YOURSELF.
	 * @param connection the connection this IRC message came from
	 * @param channel the channel this IRC message came from
	 * @param sender the user that sent this IRC message
	 * @param rawMessage the raw IRC message that was sent
	 * @param message the message that was sent
	 * @param isEmote true, if this message is an emote
	 * @param isNotice true, if this message was sent as a NOTICE
	 */
	public IRCChannelChatEvent(IRCConnection connection, IRCChannel channel, IRCUser sender, IRCMessage rawMessage, String message, boolean isEmote, boolean isNotice) {
		super(connection, rawMessage, sender, message, isNotice, channel);
		this.channel = channel;
		this.sender = sender;
		this.rawMessage = rawMessage;
		this.message = message;
		this.isEmote = isEmote;
		this.isNotice = isNotice;
	}
}
