// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.net.message.handler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.net.EiraPlayerInfo;
import net.blay09.mods.eirairc.net.message.MessageRecLiveState.SMessageRecLiveState;

public class SHandlerRecLiveState implements IMessageHandler<SMessageRecLiveState, IMessage> {
	
	@Override
	public IMessage onMessage(SMessageRecLiveState message, MessageContext ctx) {
		EiraPlayerInfo playerInfo = EiraIRC.instance.getNetHandler().getPlayerInfo(message.getUsername());
		playerInfo.isLive = message.getLiveState();
		playerInfo.isRecording = message.getRecState();
		return null;
	}

}
