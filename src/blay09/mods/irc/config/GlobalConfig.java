// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.irc.config;

import java.util.ArrayList;
import java.util.List;

public class GlobalConfig {

	public static String nick = ConfigurationHandler.DEFAULT_NICK;
	public static final List<String> colorBlackList = new ArrayList<String>();
	public static String opColor = "red";
	public static String ircColor = "gray";
	public static boolean enableNameColors = true;
	public static boolean enableAliases = true;
	public static boolean showDeathMessages = true;
	public static boolean showMinecraftJoinLeave = true;
	public static boolean showIRCJoinLeave = true;
	public static boolean allowPrivateMessages = true;
	
}
