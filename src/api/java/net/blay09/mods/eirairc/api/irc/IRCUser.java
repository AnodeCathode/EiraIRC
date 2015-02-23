// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.irc;

import java.util.Collection;

public interface IRCUser extends IRCContext {

	public Collection<IRCChannel> getChannels();
	public String getChannelModePrefix(IRCChannel channel);
	public boolean isOperator(IRCChannel channel);
	public boolean hasVoice(IRCChannel channel);

}
