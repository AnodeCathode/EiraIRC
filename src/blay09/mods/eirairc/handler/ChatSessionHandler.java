package blay09.mods.eirairc.handler;

import java.util.ArrayList;
import java.util.List;

import blay09.mods.eirairc.irc.IRCChannel;
import blay09.mods.eirairc.irc.IRCUser;

public class ChatSessionHandler {

	private String chatTarget = null;
	private final List<IRCChannel> validTargetChannels = new ArrayList<IRCChannel>();
	private final List<IRCUser> validTargetUsers = new ArrayList<IRCUser>();
	private IRCChannel suggestedChannel;
	private int targetChannelIdx = 0;
	private int targetUserIdx = -1;
	
	public String getChatTarget() {
		return chatTarget;
	}
	
	public IRCChannel getSuggestedChannel() {
		return suggestedChannel;
	}
	
	public void addTargetUser(IRCUser user) {
		if(!validTargetUsers.contains(user)) {
			validTargetUsers.add(user);
		}
	}
	
	public void addTargetChannel(IRCChannel channel) {
		if(!validTargetChannels.contains(channel)) {
			validTargetChannels.add(channel);
		}
	}
	
	public void removeTargetUser(IRCUser user) {
		validTargetUsers.remove(user);
	}
	
	public void removeTargetChannel(IRCChannel channel) {
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
		return chatTarget.startsWith("#");
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
}
