// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.handler;

import java.util.ArrayList;
import java.util.List;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IIRCChannel;
import net.blay09.mods.eirairc.api.IIRCConnection;
import net.blay09.mods.eirairc.api.IIRCContext;
import net.blay09.mods.eirairc.api.IIRCUser;
import net.blay09.mods.eirairc.irc.IRCChannel;
import net.blay09.mods.eirairc.irc.IRCUser;

public class ChatSessionHandler {

	private String chatTarget = null;
	private final List<IIRCChannel> validTargetChannels = new ArrayList<IIRCChannel>();
	private final List<IIRCUser> validTargetUsers = new ArrayList<IIRCUser>();
	private int targetChannelIdx = 0;
	private int targetUserIdx = -1;
	
	public String getChatTarget() {
		return chatTarget;
	}
	
	public void addTargetUser(IIRCUser user) {
		if(!validTargetUsers.contains(user)) {
			validTargetUsers.add(user);
		}
	}
	
	public void addTargetChannel(IIRCChannel channel) {
		if(!validTargetChannels.contains(channel)) {
			validTargetChannels.add(channel);
		}
	}
	
	public void removeTargetUser(IIRCUser user) {
		validTargetUsers.remove(user);
	}
	
	public void removeTargetChannel(IIRCChannel channel) {
		validTargetChannels.remove(channel);
	}
	
	public void setChatTarget(String chatTarget) {
		this.chatTarget = chatTarget;
	}
	
	public void setChatTarget(IRCUser user) {
		this.chatTarget = user.getIdentifier();
	}
	
	public void setChatTarget(IRCChannel channel) {
		this.chatTarget = channel.getIdentifier();
	}
	
	public boolean isChannelTarget() {
		return chatTarget.contains("#");
	}
	
	public boolean isUserTarget() {
		return !isChannelTarget();
	}
	
	public String getNextTarget(boolean users) {
		if(users) {
			if(validTargetUsers.isEmpty()) {
				return null;
			}
			targetUserIdx++;
			if(targetUserIdx >= validTargetUsers.size()) {
				targetUserIdx = 0;
			}
			return validTargetUsers.get(targetUserIdx).getIdentifier();
		} else {
			if(validTargetChannels.isEmpty()) {
				return null;
			}
			targetChannelIdx++;
			if(targetChannelIdx > validTargetChannels.size()) {
				targetChannelIdx = 0;
			}
			if(targetChannelIdx == 0) {
				return null;
			}
			return validTargetChannels.get(targetChannelIdx - 1).getIdentifier();
		}
	}

	public IIRCContext getIRCTarget() {
		if(chatTarget == null) {
			return null;
		}
		int sepIdx = chatTarget.indexOf('/');
		String targetHost = chatTarget.substring(0, sepIdx);
		IIRCConnection connection = EiraIRC.instance.getConnection(targetHost);
		if(connection == null) {
			return null;
		}
		String target = chatTarget.substring(sepIdx + 1); 
		if(isChannelTarget()) {
			return connection.getChannel(target);
		} else {
			return connection.getUser(target);
		}
	}

}
