// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.api.IIRCUser;

public class IRCUserNickChangeEvent extends IRCEvent {

	public final IIRCUser user;
	public final String oldNick;
	public final String newNick;
	
	public IRCUserNickChangeEvent(IIRCConnection connection, IIRCUser user, String oldNick, String newNick) {
		super(connection);
		this.user = user;
		this.oldNick = oldNick;
		this.newNick = newNick;
	}
}
