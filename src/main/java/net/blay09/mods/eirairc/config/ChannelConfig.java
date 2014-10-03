// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.config;

import java.util.List;

import com.google.gson.JsonObject;
import net.blay09.mods.eirairc.config.base.BotProfileImpl;
import net.blay09.mods.eirairc.config.settings.BotSettings;
import net.blay09.mods.eirairc.config.settings.GeneralSettings;
import net.blay09.mods.eirairc.config.settings.ThemeSettings;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class ChannelConfig {

	private final ServerConfig serverConfig;
	private final String name;
	private final GeneralSettings generalSettings;
	private final BotSettings botSettings;
	private final ThemeSettings theme;

	private String password;

	public ChannelConfig(ServerConfig serverConfig, String name) {
		this.serverConfig = serverConfig;
		generalSettings = new GeneralSettings(serverConfig.getGeneralSettings());
		botSettings = new BotSettings(serverConfig.getBotSettings());
		theme = new ThemeSettings(serverConfig.getTheme());

		if(serverConfig.getAddress().equals(Globals.TWITCH_SERVER)) {
			this.name = name.toLowerCase();
		} else {
			this.name = name;
		}
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public static ChannelConfig loadFromJson(ServerConfig serverConfig, JsonObject object) {
		ChannelConfig config = new ChannelConfig(serverConfig, object.get("name").getAsString());
		if(object.has("password")) {
			config.password = object.get("password").getAsString();
		}
		if(object.has("bot")) {
			config.botSettings.load(object.getAsJsonObject("bot"));
		}
		if(object.has("theme")) {
			config.theme.load(object.getAsJsonObject("theme"));
		}
		if(object.has("settings")) {
			config.generalSettings.load(object.getAsJsonObject("settings"));
		}
		return config;
	}

	public void loadLegacy(Configuration config, ConfigCategory category) {
		String categoryName = category.getQualifiedName();
		password = Utils.unquote(config.get(categoryName, "password", "").getString());
	}

	public void handleConfigCommand(ICommandSender sender, String key) {
		String value = null;
		if(value != null) {
			Utils.sendLocalizedMessage(sender, "irc.config.lookup", name, key, value);
		} else {
			Utils.sendLocalizedMessage(sender, "irc.config.invalidOption", name, key);
		}
	}

	public void handleConfigCommand(ICommandSender sender, String key, String value) {
		if(true) {
			Utils.sendLocalizedMessage(sender, "irc.config.invalidOption", name, key, value);
			return;
		}
		Utils.sendLocalizedMessage(sender, "irc.config.change", name, key, value);
		ConfigurationHandler.save();
	}

	public static void addOptionsToList(List<String> list) {
	}

	public ServerConfig getServerConfig() {
		return serverConfig;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public static void addValuesToList(List<String> list, String option) {
	}

	public ThemeSettings getTheme() {
		return theme;
	}

	public GeneralSettings getGeneralSettings() {
		return generalSettings;
	}

	public BotSettings getBotSettings() {
		return botSettings;
	}
}
