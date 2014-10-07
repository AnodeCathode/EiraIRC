package net.blay09.mods.eirairc.config.settings;

import com.google.gson.JsonObject;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by Blay09 on 29.09.2014.
 */
public class ThemeSettings {

	private static final String[] VALID_COLOR_CODES = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
	private static EnumChatFormatting getColorFromCode(char code) {
		switch(code) {
			case '0': return EnumChatFormatting.BLACK;
			case '1': return EnumChatFormatting.DARK_BLUE;
			case '2': return EnumChatFormatting.DARK_GREEN;
			case '3': return EnumChatFormatting.DARK_AQUA;
			case '4': return EnumChatFormatting.DARK_RED;
			case '5': return EnumChatFormatting.DARK_PURPLE;
			case '6': return EnumChatFormatting.GOLD;
			case '7': return EnumChatFormatting.GRAY;
			case '8': return EnumChatFormatting.DARK_GRAY;
			case '9': return EnumChatFormatting.BLUE;
			case 'a': return EnumChatFormatting.GREEN;
			case 'b': return EnumChatFormatting.AQUA;
			case 'c': return EnumChatFormatting.RED;
			case 'd': return EnumChatFormatting.LIGHT_PURPLE;
			case 'e': return EnumChatFormatting.YELLOW;
			case 'f': return EnumChatFormatting.WHITE;
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

	public boolean hasColor(ThemeColorComponent component) {
		return colors.containsKey(component);
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
			Property property = dummyConfig.get("theme", ThemeColorComponent.values[i].name, String.valueOf(parent.getColor(ThemeColorComponent.values[i]).getFormattingCode()));
			property.setLanguageKey(ThemeColorComponent.values[i].langKey);
			property.setValidValues(VALID_COLOR_CODES);
			if(colors.containsKey(ThemeColorComponent.values[i])) {
				property.set(String.valueOf(colors.get(ThemeColorComponent.values[i]).getFormattingCode()));
			}
		}
		return dummyConfig;
	}

	public void load(Configuration config, String category, boolean defaultValues) {
		colors.clear();
		for(int i = 0; i < ThemeColorComponent.values().length; i++) {
			if(defaultValues || config.hasKey(category, ThemeColorComponent.values[i].name)) {
				String value = config.getString(ThemeColorComponent.values[i].name, category, String.valueOf(ThemeColorComponent.values[i].defaultValue.getFormattingCode()), "", VALID_COLOR_CODES, ThemeColorComponent.values[i].langKey);
				if(!value.isEmpty()) {
					EnumChatFormatting colorValue = getColorFromCode(value.charAt(0));
					if(defaultValues || colorValue != parent.getColor(ThemeColorComponent.values[i])) {
						colors.put(ThemeColorComponent.values[i], colorValue);
					}
				}
			}
		}
	}

	public void load(JsonObject object) {
		for(int i = 0; i < ThemeColorComponent.values().length; i++) {
			if(object.has(ThemeColorComponent.values[i].name)) {
				colors.put(ThemeColorComponent.values[i], getColorFromCode(object.get(ThemeColorComponent.values[i].name).getAsCharacter()));
			}
		}
	}

	public JsonObject toJsonObject() {
		if(colors.isEmpty()) {
			return null;
		}
		JsonObject object = new JsonObject();
		for(Map.Entry<ThemeColorComponent, EnumChatFormatting> entry : colors.entrySet()) {
			object.addProperty(entry.getKey().name, entry.getValue().getFormattingCode());
		}
		return object;
	}

	public void save(Configuration config, String category) {
		for(Map.Entry<ThemeColorComponent, EnumChatFormatting> entry : colors.entrySet()) {
			config.get(category, entry.getKey().name, "", I18n.format(entry.getKey().langKey + ".tooltip")).set(entry.getValue().getFormattingCode());
		}
	}

	public void loadLegacy(Configuration legacyConfig, String categoryName) {
		if(categoryName != null) {
			String emoteColor = Utils.unquote(legacyConfig.get(categoryName, "emoteColor", "").getString());
			if(!emoteColor.isEmpty()) {
				colors.put(ThemeColorComponent.emoteTextColor, Utils.getColorFormatting(emoteColor));
			}
			String ircColor = Utils.unquote(legacyConfig.get(categoryName, "ircColor", "").getString());
			if(!ircColor.isEmpty()) {
				colors.put(ThemeColorComponent.ircNameColor, Utils.getColorFormatting(ircColor));
			}
		} else {
			colors.put(ThemeColorComponent.emoteTextColor, Utils.getColorFormatting(Utils.unquote(legacyConfig.get("display", "emoteColor", "gold").getString())));
			colors.put(ThemeColorComponent.mcNameColor, Utils.getColorFormatting(Utils.unquote(legacyConfig.get("display", "defaultColor", "white").getString())));
			colors.put(ThemeColorComponent.mcOpNameColor, Utils.getColorFormatting(Utils.unquote(legacyConfig.get("display", "opColor", "red").getString())));
			colors.put(ThemeColorComponent.ircNameColor, Utils.getColorFormatting(Utils.unquote(legacyConfig.get("display", "ircColor", "gray").getString())));
			colors.put(ThemeColorComponent.ircPrivateNameColor, Utils.getColorFormatting(Utils.unquote(legacyConfig.get("display", "ircPrivateColor", "gray").getString())));
			colors.put(ThemeColorComponent.ircVoiceNameColor, Utils.getColorFormatting(Utils.unquote(legacyConfig.get("display", "ircVoiceColor", "gray").getString())));
			colors.put(ThemeColorComponent.ircOpNameColor, Utils.getColorFormatting(Utils.unquote(legacyConfig.get("display", "ircOpColor", "gold").getString())));
			colors.put(ThemeColorComponent.ircNoticeTextColor, Utils.getColorFormatting(Utils.unquote(legacyConfig.get("display", "ircNoticeColor", "gray").getString())));
		}
	}

}
