// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.config;

import java.io.File;

import net.blay09.mods.eirairc.util.Utils;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class DisplayFormatConfig {

	private static final String CATEGORY_GENERAL = "general";
	private static final String CATEGORY_FORMAT = "format";
	private static final String CATEGORY_FORMAT_IRC = "irc";
	private static final String CATEGORY_FORMAT_MC = "minecraft";
	
	public static final String DEFAULT_FORMAT = "S-Light";
	
	private final Configuration config;
	
	private String name;
	public String mcChannelMessage;
	public String mcChannelEmote;
	public String mcChannelNotice;
	public String mcPrivateMessage;
	public String mcPrivateNotice;
	public String mcPrivateEmote;
	public String mcSendChannelMessage;
	public String mcSendChannelEmote;
	public String mcSendPrivateMessage;
	public String mcSendPrivateEmote;
	public String mcUserJoin;
	public String mcUserLeave;
	public String mcUserQuit;
	public String mcUserNickChange;
	public String ircChannelMessage;
	public String ircChannelEmote;
	public String ircPrivateMessage;
	public String ircPrivateEmote;
	public String ircPlayerJoin;
	public String ircPlayerLeave;
	public String ircPlayerNickChange;
	public String ircBroadcastMessage;
	
	public DisplayFormatConfig(File file) {
		config = new Configuration(file);
		name = Utils.unquote(config.get(CATEGORY_GENERAL, "name", file.getName().substring(0, file.getName().length() - 4)).getString());
	}

	public void loadFormats() {
		loadMinecraftFormats(config);
		loadIRCFormats(config);
	}
	
	public void loadMinecraftFormats(Configuration config) {
		String categoryName = CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC;
		mcChannelMessage = Utils.unquote(config.get(categoryName, "mcChannelMessage", "").getString());
		mcChannelEmote = Utils.unquote(config.get(categoryName, "mcChannelEmote", "").getString());
		mcChannelNotice = Utils.unquote(config.get(categoryName, "mcChannelNotice", "").getString());
		mcPrivateMessage = Utils.unquote(config.get(categoryName, "mcPrivateMessage", "").getString());
		mcPrivateEmote = Utils.unquote(config.get(categoryName, "mcPrivateEmote", "").getString());
		mcPrivateNotice = Utils.unquote(config.get(categoryName, "mcPrivateNotice", "").getString());
		
		mcSendChannelMessage = Utils.unquote(config.get(categoryName, "mcSendChannelMessage", "").getString());
		mcSendChannelEmote = Utils.unquote(config.get(categoryName, "mcSendChannelEmote", "").getString());
		mcSendPrivateMessage = Utils.unquote(config.get(categoryName, "mcSendPrivateMessage", "").getString());
		mcSendPrivateEmote = Utils.unquote(config.get(categoryName, "mcSendPrivateEmote", "").getString());
		
		mcUserJoin = Utils.unquote(config.get(categoryName, "mcUserJoin", "").getString());
		mcUserLeave = Utils.unquote(config.get(categoryName, "mcUserLeave", "").getString());
		mcUserQuit = Utils.unquote(config.get(categoryName, "mcUserQuit", "").getString());
		mcUserNickChange = Utils.unquote(config.get(categoryName, "mcUserNickChange", "").getString());
	}
	
	public void loadIRCFormats(Configuration config) {
		String categoryName = CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_IRC;
		ircChannelMessage = Utils.unquote(config.get(categoryName, "ircChannelMessage", "").getString());
		ircChannelEmote = Utils.unquote(config.get(categoryName, "ircChannelEmote", "").getString());
		ircPrivateMessage = Utils.unquote(config.get(categoryName, "ircPrivateMessage", "").getString());
		ircPrivateEmote = Utils.unquote(config.get(categoryName, "ircPrivateEmote", "").getString());
		
		ircPlayerJoin = Utils.unquote(config.get(categoryName, "ircPlayerJoin", "").getString());
		ircPlayerLeave = Utils.unquote(config.get(categoryName, "ircPlayerLeave", "").getString());
		ircPlayerNickChange = Utils.unquote(config.get(categoryName, "ircPlayerNickChange", "").getString());
		
		ircBroadcastMessage = Utils.unquote(config.get(categoryName, "ircBroadcastMessage", "").getString());
	}
	
	public String getName() {
		return name;
	}

	public static void setupDefaultFormats(File displayDir) {
		Configuration config = new Configuration(new File(displayDir, "classic.cfg"));
		config.get(CATEGORY_GENERAL, "name", Utils.quote("Classic"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcChannelMessage", Utils.quote("[{CHANNEL}] <{NICK}> {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcChannelEmote", Utils.quote("[{CHANNEL}] * {NICK} {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcChannelNotice", Utils.quote("[{CHANNEL}] ({NICK}) {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcPrivateMessage", Utils.quote("[Private] <{NICK}> {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcPrivateEmote", Utils.quote("[Private] * {NICK} {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcPrivateNotice", Utils.quote("[Private] ({NICK}) {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcSendChannelMessage", Utils.quote("[{CHANNEL}] <{NICK}> {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcSendChannelEmote", Utils.quote("[{CHANNEL}] * {NICK} {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcSendPrivateMessage", Utils.quote("[->{CHANNEL}] <{NICK}> {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcSendPrivateEmote", Utils.quote("[->{CHANNEL}] * {NICK} {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_IRC, "ircChannelMessage", Utils.quote("<{NICK}> {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_IRC, "ircChannelEmote", Utils.quote("* {NICK} {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_IRC, "ircPrivateMessage", Utils.quote("<{NICK}> {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_IRC, "ircPrivateEmote", Utils.quote("* {NICK} {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_IRC, "ircBroadcastMessage", Utils.quote("*** {MESSAGE} ***"));
		config.save();
		
		config = new Configuration(new File(displayDir, "light.cfg"));
		config.get(CATEGORY_GENERAL, "name", Utils.quote("Light"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcChannelMessage", Utils.quote("[ <{NICK}> {MESSAGE} ]"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcChannelEmote", Utils.quote("[ * {NICK} {MESSAGE} ]"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcChannelNotice", Utils.quote("[ ({NICK}) {MESSAGE} ]"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcPrivateMessage", Utils.quote("[[ <{NICK}> {MESSAGE} ]]"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcPrivateEmote", Utils.quote("[[ {NICK} {MESSAGE} ]]"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcPrivateNotice", Utils.quote("[[ ({NICK}) {MESSAGE} ]]"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcSendChannelMessage", Utils.quote("[ <{NICK}> {MESSAGE} ]"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcSendChannelEmote", Utils.quote("[ * {NICK} {MESSAGE} ]"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcSendPrivateMessage", Utils.quote("[[->{CHANNEL}] <{NICK}> {MESSAGE} ]"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcSendPrivateEmote", Utils.quote("[[->{CHANNEL}] {NICK} {MESSAGE} ]"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_IRC, "ircChannelMessage", Utils.quote("<{NICK}> {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_IRC, "ircChannelEmote", Utils.quote("* {NICK} {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_IRC, "ircPrivateMessage", Utils.quote("<{NICK}> {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_IRC, "ircPrivateEmote", Utils.quote("* {NICK} {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_IRC, "ircBroadcastMessage", Utils.quote("*** {MESSAGE} ***"));
		config.save();
		
		config = new Configuration(new File(displayDir, "s-light.cfg"));
		config.get(CATEGORY_GENERAL, "name", Utils.quote("S-Light"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcChannelMessage", Utils.quote("[{NICK}] {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcChannelEmote", Utils.quote("[ * {NICK} {MESSAGE} ]"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcChannelNotice", Utils.quote("({NICK}) {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcPrivateMessage", Utils.quote("[[{NICK}]] {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcPrivateEmote", Utils.quote("[[ {NICK} {MESSAGE} ]]"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcPrivateNotice", Utils.quote("[({NICK})] {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcSendChannelMessage", Utils.quote("[{NICK}] {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcSendChannelEmote", Utils.quote("[ * {NICK} {MESSAGE} ]"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcSendPrivateMessage", Utils.quote("[[>{CHANNEL}]{NICK}] {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcSendPrivateEmote", Utils.quote("[[>{CHANNEL}] {NICK} {MESSAGE} ]"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_IRC, "ircChannelMessage", Utils.quote("<{NICK}> {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_IRC, "ircChannelEmote", Utils.quote("* {NICK} {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_IRC, "ircPrivateMessage", Utils.quote("<{NICK}> {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_IRC, "ircPrivateEmote", Utils.quote("* {NICK} {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_IRC, "ircBroadcastMessage", Utils.quote("*** {MESSAGE} ***"));
		config.save();
		
		config = new Configuration(new File(displayDir, "minecraft.cfg"));
		config.get(CATEGORY_GENERAL, "name", Utils.quote("Minecraft"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcChannelMessage", Utils.quote("<{NICK}> {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcChannelEmote", Utils.quote("* {NICK} {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcChannelNotice", Utils.quote("({NICK}) {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcPrivateMessage", Utils.quote("[P] <{NICK}> {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcPrivateEmote", Utils.quote("[P] * {NICK} {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcPrivateNotice", Utils.quote("[P] (NICK) {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcSendChannelMessage", Utils.quote("<{NICK}> {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcSendChannelEmote", Utils.quote("* {NICK} {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcSendPrivateMessage", Utils.quote("[{CHANNEL}] <{NICK}> {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcSendPrivateEmote", Utils.quote("[{CHANNEL}] * {NICK} {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_IRC, "ircChannelMessage", Utils.quote("<{NICK}> {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_IRC, "ircChannelEmote", Utils.quote("* {NICK} {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_IRC, "ircPrivateMessage", Utils.quote("<{NICK}> {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_IRC, "ircPrivateEmote", Utils.quote("* {NICK} {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_IRC, "ircBroadcastMessage", Utils.quote("[Server] {MESSAGE}"));
		config.save();
		
		config = new Configuration(new File(displayDir, "detail.cfg"));
		config.get(CATEGORY_GENERAL, "name", Utils.quote("Detail"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcChannelMessage", Utils.quote("[{SERVER}/{CHANNEL}] <{NICK}> {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcChannelEmote", Utils.quote("[{SERVER}/{CHANNEL}] * {NICK} {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcChannelNotice", Utils.quote("[{SERVER}/{CHANNEL}] ({NICK}) {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcPrivateMessage", Utils.quote("[{SERVER}] <{NICK}> {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcPrivateEmote", Utils.quote("[{SERVER}] * {NICK} {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcPrivateNotice", Utils.quote("[{SERVER}] ({NICK}) {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcSendChannelMessage", Utils.quote("[{SERVER}/{CHANNEL}] <{NICK}> {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcSendChannelEmote", Utils.quote("[{SERVER}/{CHANNEL}] * {NICK} {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcSendPrivateMessage", Utils.quote("[{SERVER}/{CHANNEL}]  <{NICK}> {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_MC, "mcSendPrivateEmote", Utils.quote("[{SERVER}/{CHANNEL}] * {NICK} {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_IRC, "ircChannelMessage", Utils.quote("<{NICK}> {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_IRC, "ircChannelEmote", Utils.quote("* {NICK} {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_IRC, "ircPrivateMessage", Utils.quote("<{NICK}> {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_IRC, "ircPrivateEmote", Utils.quote("* {NICK} {MESSAGE}"));
		config.get(CATEGORY_FORMAT + Configuration.CATEGORY_SPLITTER + CATEGORY_FORMAT_IRC, "ircBroadcastMessage", Utils.quote("[{SERVER}] *** {MESSAGE} ***"));
		config.save();
	}

}
