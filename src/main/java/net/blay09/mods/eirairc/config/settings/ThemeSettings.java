package net.blay09.mods.eirairc.config.settings;

import com.google.gson.JsonObject;
import net.blay09.mods.eirairc.util.I19n;
import net.blay09.mods.eirairc.util.IRCFormatting;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;


public class ThemeSettings {

	private static final String[] VALID_COLOR_CODES = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"};
	private static EnumChatFormatting getColorFromCode(int colorCode) {
		switch(colorCode) {
			case 0: return EnumChatFormatting.BLACK;
			case 1: return EnumChatFormatting.DARK_BLUE;
			case 2: return EnumChatFormatting.DARK_GREEN;
			case 3: return EnumChatFormatting.DARK_AQUA;
			case 4: return EnumChatFormatting.DARK_RED;
			case 5: return EnumChatFormatting.DARK_PURPLE;
			case 6: return EnumChatFormatting.GOLD;
			case 7: return EnumChatFormatting.GRAY;
			case 8: return EnumChatFormatting.DARK_GRAY;
			case 9: return EnumChatFormatting.BLUE;
			case 10: return EnumChatFormatting.GREEN;
			case 11: return EnumChatFormatting.AQUA;
			case 12: return EnumChatFormatting.RED;
			case 13: return EnumChatFormatting.LIGHT_PURPLE;
			case 14: return EnumChatFormatting.YELLOW;
			case 15: return EnumChatFormatting.WHITE;
		}
		return null;
	}

	private final ThemeSettings parent;
	private final EnumMap<ThemeColorComponent, EnumChatFormatting> colors = new EnumMap<ThemeColorComponent, EnumChatFormatting>(ThemeColorComponent.class);

	private Configuration dummyConfig;

	public ThemeSettings(ThemeSettings parent) {
		this.parent = parent;
	}

	public EnumChatFormatting getColor(ThemeColorComponent component) {
		EnumChatFormatting color = colors.get(component);
		if(color == null && parent != null) {
			return parent.getColor(component);
		}
		return color;
	}

	public void pushDummyConfig() {
		if(dummyConfig != null) {
			load(dummyConfig, "theme", false);
			dummyConfig = null;
		}
	}

	public Configuration pullDummyConfig() {
		dummyConfig = new Configuration();
		for(int i = 0; i < ThemeColorComponent.values().length; i++) {
			EnumChatFormatting defaultColor = parent.getColor(ThemeColorComponent.values[i]);
			if(defaultColor == null) {
				defaultColor = EnumChatFormatting.WHITE;
			}
			Property property = dummyConfig.get("theme", ThemeColorComponent.values[i].name, String.valueOf(defaultColor.func_175746_b())); // getColorIndex
			property.setLanguageKey(ThemeColorComponent.values[i].langKey);
			property.setValidValues(VALID_COLOR_CODES);
			if(colors.containsKey(ThemeColorComponent.values[i])) {
				property.set(String.valueOf(colors.get(ThemeColorComponent.values[i]).func_175746_b())); // getColorIndex
			}
		}
		return dummyConfig;
	}

	public void load(Configuration config, String category, boolean defaultValues) {
		colors.clear();
		for(int i = 0; i < ThemeColorComponent.values().length; i++) {
			if(defaultValues || config.hasKey(category, ThemeColorComponent.values[i].name)) {
				String value = config.getString(ThemeColorComponent.values[i].name, category, String.valueOf(ThemeColorComponent.values[i].defaultValue.func_175746_b()), I19n.format(ThemeColorComponent.values[i].langKey + ".tooltip"), VALID_COLOR_CODES, ThemeColorComponent.values[i].langKey); // getColorIndex
				if(!value.isEmpty()) {
					try {
						EnumChatFormatting colorValue = getColorFromCode(Integer.parseInt(value));
						if (defaultValues || colorValue != parent.getColor(ThemeColorComponent.values[i])) {
							colors.put(ThemeColorComponent.values[i], colorValue);
						}
					} catch (NumberFormatException ignored) {}
				}
			}
		}
	}

	public void load(JsonObject object) {
		for(int i = 0; i < ThemeColorComponent.values().length; i++) {
			if(object.has(ThemeColorComponent.values[i].name)) {
				colors.put(ThemeColorComponent.values[i], getColorFromCode(object.get(ThemeColorComponent.values[i].name).getAsInt()));
			}
		}
	}

	public JsonObject toJsonObject() {
		if(colors.isEmpty()) {
			return null;
		}
		JsonObject object = new JsonObject();
		for(Map.Entry<ThemeColorComponent, EnumChatFormatting> entry : colors.entrySet()) {
			object.addProperty(entry.getKey().name, entry.getValue().func_175746_b()); // getColorIndex
		}
		return object;
	}

	public void save(Configuration config, String category) {
		for(Map.Entry<ThemeColorComponent, EnumChatFormatting> entry : colors.entrySet()) {
			config.get(category, entry.getKey().name, "", I19n.format(entry.getKey().langKey + ".tooltip")).set(entry.getValue().func_175746_b()); // getColorIndex
		}
	}

	public void loadLegacy(Configuration legacyConfig, String categoryName) {
		if(categoryName != null) {
			String emoteColor = Utils.unquote(legacyConfig.get(categoryName, "emoteColor", "").getString());
			if(!emoteColor.isEmpty()) {
				colors.put(ThemeColorComponent.emoteTextColor, IRCFormatting.getColorFormattingLegacy(emoteColor));
			}
			String ircColor = Utils.unquote(legacyConfig.get(categoryName, "ircColor", "").getString());
			if(!ircColor.isEmpty()) {
				colors.put(ThemeColorComponent.ircNameColor, IRCFormatting.getColorFormattingLegacy(ircColor));
			}
		} else {
			colors.put(ThemeColorComponent.emoteTextColor, IRCFormatting.getColorFormattingLegacy(Utils.unquote(legacyConfig.get("display", "emoteColor", "gold").getString())));
			colors.put(ThemeColorComponent.mcNameColor, IRCFormatting.getColorFormattingLegacy(Utils.unquote(legacyConfig.get("display", "defaultColor", "white").getString())));
			colors.put(ThemeColorComponent.mcOpNameColor, IRCFormatting.getColorFormattingLegacy(Utils.unquote(legacyConfig.get("display", "opColor", "red").getString())));
			colors.put(ThemeColorComponent.ircNameColor, IRCFormatting.getColorFormattingLegacy(Utils.unquote(legacyConfig.get("display", "ircColor", "gray").getString())));
			colors.put(ThemeColorComponent.ircPrivateNameColor, IRCFormatting.getColorFormattingLegacy(Utils.unquote(legacyConfig.get("display", "ircPrivateColor", "gray").getString())));
			colors.put(ThemeColorComponent.ircVoiceNameColor, IRCFormatting.getColorFormattingLegacy(Utils.unquote(legacyConfig.get("display", "ircVoiceColor", "gray").getString())));
			colors.put(ThemeColorComponent.ircOpNameColor, IRCFormatting.getColorFormattingLegacy(Utils.unquote(legacyConfig.get("display", "ircOpColor", "gold").getString())));
			colors.put(ThemeColorComponent.ircNoticeTextColor, IRCFormatting.getColorFormattingLegacy(Utils.unquote(legacyConfig.get("display", "ircNoticeColor", "gray").getString())));
		}
	}

	public String handleConfigCommand(ICommandSender sender, String key) {
		ThemeColorComponent component = ThemeColorComponent.fromName(key);
		if(component != null) {
			if (colors.containsKey(component)) {
				return colors.get(component).name();
			} else {
				return "<inherit>";
			}
		}
		return null;
	}
	public boolean handleConfigCommand(ICommandSender sender, String key, String value) {
		ThemeColorComponent component = ThemeColorComponent.fromName(key);
		if(component != null) {
			colors.put(component, EnumChatFormatting.getValueByName(value));
			return true;
		}
		return false;
	}
	public static void addOptionsToList(List<String> list, String option, boolean autoCompleteOption) {
		if(option == null) {
			for(ThemeColorComponent component : ThemeColorComponent.values) {
				list.add(component.name);
			}
		} else {
			if(ThemeColorComponent.fromName(option) != null) {
				IRCFormatting.addValidColorsToList(list);
			}
		}
	}
}
