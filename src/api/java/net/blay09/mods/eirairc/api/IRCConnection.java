// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api;

import net.blay09.mods.eirairc.api.bot.IRCBot;

import java.util.Collection;

public interface IRCConnection {

	public boolean irc(String irc);
	public void nick(String nick);
	public void join(String channelName, String password);
	public void part(String channelName);
	public void kick(String channelName, String nick, String reason);
	public void mode(String channelName, String mode);
	public void mode(String channelName, String nick, String mode);
	public void topic(String channelName, String topic);
	public void disconnect(String quitMessage);
	
	public String getServerType();
	public String getChannelTypes();
	public String getChannelUserModes();
	public String getChannelUserModePrefixes();
	public Collection<IRCChannel> getChannels();
	public IRCChannel getChannel(String name);
	public IRCUser getUser(String name);
	public IRCUser getOrCreateUser(String name);
	public String getHost();
	public int[] getPorts();
	public IRCBot getBot();
	public String getIdentifier();
	public String getNick();

}
