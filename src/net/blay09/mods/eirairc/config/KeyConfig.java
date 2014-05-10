// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.config;

import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.minecraftforge.common.Configuration;

import org.lwjgl.input.Keyboard;

public class KeyConfig {
	
	private static final String CATEGORY = ConfigurationHandler.CATEGORY_KEYBINDS;

	public static final int IDX_OPENSETTINGS = 0;
	public static final int IDX_TOGGLETARGET = 1;
	public static final int IDX_SCREENSHOTSHARE = 2;
	public static final int IDX_TOGGLERECORDING = 3;
	public static final int IDX_TOGGLELIVE = 4;
	public static final int IDX_OPENSCREENSHOTS = 5;
	
	public static int screenshotShare = -1;
	public static int openScreenshots = -1;
	public static int toggleRecording = Keyboard.KEY_F9;
	public static int toggleLive = -1;
	public static int toggleTarget = Keyboard.KEY_TAB;
	public static int openMenu = Keyboard.KEY_I;
	
	public static void load(Configuration config) {
		openMenu = config.get(CATEGORY, "keyMenu", openMenu).getInt();
		toggleTarget = config.get(CATEGORY, "keyToggleTarget", toggleTarget).getInt();
		toggleLive = config.get(CATEGORY, "keyToggleLive", toggleLive).getInt();
		toggleRecording = config.get(CATEGORY, "keyToggleRecording", toggleRecording).getInt();
		screenshotShare = config.get(CATEGORY, "keyScreenshotShare", screenshotShare).getInt();
		openScreenshots = config.get(CATEGORY, "keyOpenScreenshots", openScreenshots).getInt();
	}

	public static void save(Configuration config) {
		config.get(CATEGORY, "keyMenu", openMenu).set(openMenu);
		config.get(CATEGORY, "keyToggleTarget", toggleTarget).set(toggleTarget);
		config.get(CATEGORY, "keyToggleLive", toggleLive).set(toggleLive);
		config.get(CATEGORY, "keyToggleRecording", toggleRecording).set(toggleRecording);
		config.get(CATEGORY, "keyScreenshotShare", screenshotShare).set(screenshotShare);
		config.get(CATEGORY, "keyOpenScreenshots", openScreenshots).set(openScreenshots);
	}
	
}
