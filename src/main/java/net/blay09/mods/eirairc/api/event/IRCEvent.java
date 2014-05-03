package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.base.IIRCConnection;
import net.blay09.mods.eirairc.api.bot.IIRCBot;
import cpw.mods.fml.common.eventhandler.Event;

public abstract class IRCEvent extends Event {

	public final IIRCConnection connection;
	public final IIRCBot bot;
	
	public IRCEvent(IIRCConnection connection) {
		this.connection = connection;
		this.bot = connection.getBot();
	}
	
}
