package net.blay09.mods.eirairc.api.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.blay09.mods.eirairc.api.IIRCContext;
import net.minecraft.command.ICommandSender;

/**
 * Created by Blay09 on 01.08.2014.
 */
public class RelayChat extends Event {

	public final IIRCContext target;
	public final ICommandSender sender;
	public final String message;
	public final boolean isEmote;
	public final boolean isNotice;

	public RelayChat(ICommandSender sender, String message) {
		this(sender, message, false, false, null);
	}

	public RelayChat(ICommandSender sender, String message, boolean isEmote) {
		this(sender, message, isEmote, false, null);
	}

	public RelayChat(ICommandSender sender, String message, boolean isEmote, boolean isNotice) {
		this(sender, message, isEmote, isNotice, null);
	}

	public RelayChat(ICommandSender sender, String message, boolean isEmote, boolean isNotice, IIRCContext target) {
		this.sender = sender;
		this.message = message;
		this.isEmote = isEmote;
		this.isNotice = isNotice;
		this.target = target;
	}

}
