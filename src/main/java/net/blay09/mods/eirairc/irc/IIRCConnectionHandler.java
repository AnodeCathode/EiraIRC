// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.irc;

public interface IIRCConnectionHandler {

	public void onConnecting(IRCConnection connection);
	public void onConnected(IRCConnection connection);
	public void onDisconnected(IRCConnection connection);
	public void onIRCError(IRCConnection connection, int errorCode, String line, String[] cmd);
	public void onChannelJoined(IRCConnection connection, IRCChannel channel);
	public void onChannelLeft(IRCConnection connection, IRCChannel channel);
	
}
