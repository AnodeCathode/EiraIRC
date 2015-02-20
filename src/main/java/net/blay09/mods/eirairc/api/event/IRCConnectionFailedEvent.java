// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.IRCConnection;

public class IRCConnectionFailedEvent extends IRCEvent {

	public final Exception exception;

	public IRCConnectionFailedEvent(IRCConnection connection, Exception exception) {
		super(connection);
		this.exception = exception;
	}

}
