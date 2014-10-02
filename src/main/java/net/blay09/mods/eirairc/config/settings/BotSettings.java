package net.blay09.mods.eirairc.config.settings;

import net.minecraftforge.common.config.Configuration;

import java.util.EnumMap;

/**
 * Created by Blay09 on 02.10.2014.
 */
public class BotSettings {

	private final BotSettings parent;

	private final EnumMap<BotStringComponent, String> strings = new EnumMap<BotStringComponent, String>(BotStringComponent.class);
	private final EnumMap<BotBooleanComponent, String> booleans = new EnumMap<BotBooleanComponent, String>(BotBooleanComponent.class);

	public BotSettings(BotSettings parent) {
		this.parent = parent;
	}

	public void load(Configuration config, String category, boolean defaultValues) {

	}

	public void save(Configuration config, String category) {

	}

	public void loadLegacy(Configuration legacyConfig) {

	}
}