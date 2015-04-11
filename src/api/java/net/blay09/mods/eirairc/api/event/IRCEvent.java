// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.
package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Base class for events based on an IRC connection.
 */
public abstract class IRCEvent extends Event {

	/**
	 * the connection this event is based on
	 */
	public final IRCConnection connection;

	/**
	 * the bot this event is based on
	 */
	public final IRCBot bot;

	/**
	 * INTERNAL EVENT. YOU SHOULD NOT POST THIS YOURSELF.
	 * @param connection the connection this event is based on
	 */
	public IRCEvent(IRCConnection connection) {
		this.connection = connection;
		this.bot = connection.getBot();
	}
	
}
