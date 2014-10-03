package net.blay09.mods.eirairc.config.settings;

import com.google.gson.JsonObject;
import net.blay09.mods.eirairc.config.base.MessageFormatConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraftforge.common.config.Configuration;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by Blay09 on 02.10.2014.
 */
public class BotSettings {

	private final BotSettings parent;

	private final EnumMap<BotStringComponent, String> strings = new EnumMap<BotStringComponent, String>(BotStringComponent.class);
	private final EnumMap<BotBooleanComponent, Boolean> booleans = new EnumMap<BotBooleanComponent, Boolean>(BotBooleanComponent.class);

	public BotSettings(BotSettings parent) {
		this.parent = parent;
	}

	public String getString(BotStringComponent component) {
		if(!strings.containsKey(component)) {
			if(parent != null) {
				return parent.getString(component);
			}
			return component.defaultValue;
		}
		return strings.get(component);
	}

	public void setString(BotStringComponent component, String value) {
		strings.put(component, value);
	}

	public boolean getBoolean(BotBooleanComponent component) {
		if(!booleans.containsKey(component)) {
			if(parent != null) {
				return parent.getBoolean(component);
			}
			return component.defaultValue;
		}
		return booleans.get(component);
	}

	public MessageFormatConfig getMessageFormat() {
		return ConfigurationHandler.getMessageFormat(getString(BotStringComponent.MessageFormat));
	}

	public void load(Configuration config, String category, boolean defaultValues) {
		for(int i = 0; i < BotStringComponent.values().length; i++) {
			if(defaultValues || config.hasKey(category, BotStringComponent.values[i].name)) {
				strings.put(BotStringComponent.values[i], config.getString(BotStringComponent.values[i].name, category, BotStringComponent.values[i].defaultValue, ""));
			}
		}
		for(int i = 0; i < BotBooleanComponent.values().length; i++) {
			if(defaultValues || config.hasKey(category, BotBooleanComponent.values[i].name)) {
				booleans.put(BotBooleanComponent.values[i], config.getBoolean(BotBooleanComponent.values[i].name, category, BotBooleanComponent.values[i].defaultValue, ""));
			}
		}
	}

	public void load(JsonObject object) {
		for(int i = 0; i < BotStringComponent.values().length; i++) {
			if(object.has(BotStringComponent.values[i].name)) {
				strings.put(BotStringComponent.values[i], object.get(BotStringComponent.values[i].name).getAsString());
			}
		}
		for(int i = 0; i < BotBooleanComponent.values().length; i++) {
			if(object.has(BotBooleanComponent.values[i].name)) {
				booleans.put(BotBooleanComponent.values[i], object.get(BotBooleanComponent.values[i].name).getAsBoolean());
			}
		}
	}

	public JsonObject toJsonObject() {
		if(strings.isEmpty() && booleans.isEmpty()) {
			return null;
		}
		JsonObject object = new JsonObject();
		for(Map.Entry<BotStringComponent, String> entry : strings.entrySet()) {
			object.addProperty(entry.getKey().name, entry.getValue());
		}
		for(Map.Entry<BotBooleanComponent, Boolean> entry : booleans.entrySet()) {
			object.addProperty(entry.getKey().name, entry.getValue());
		}
		return object;
	}

	public void save(Configuration config, String category) {
		for(Map.Entry<BotStringComponent, String> entry : strings.entrySet()) {
			config.get(category, entry.getKey().name, "", entry.getKey().comment).set(entry.getValue());
		}
		for(Map.Entry<BotBooleanComponent, Boolean> entry : booleans.entrySet()) {
			config.get(category, entry.getKey().name, false, entry.getKey().comment).set(entry.getValue());
		}
	}

	public void loadLegacy(Configuration legacyConfig, String category) {
		if(category != null) {
			strings.put(BotStringComponent.Description, Utils.unquote(legacyConfig.get(category, "description", BotStringComponent.Description.defaultValue).getString()));
			strings.put(BotStringComponent.Ident, Utils.unquote(legacyConfig.get(category, "ident", BotStringComponent.Ident.defaultValue).getString()));
			String quitMessage = Utils.unquote(legacyConfig.get(category, "quitMessage", "").getString());
			if(!quitMessage.isEmpty()) {
				strings.put(BotStringComponent.QuitMessage, quitMessage);
			}
		} else {
			strings.put(BotStringComponent.NickFormat, Utils.unquote(legacyConfig.get("serveronly", "nickPrefix", "").getString()) + "%s" + Utils.unquote(legacyConfig.get("serveronly", "nickSuffix", "").getString()));
			booleans.put(BotBooleanComponent.HideNotices, legacyConfig.get("display", "hideNotices", BotBooleanComponent.HideNotices.defaultValue).getBoolean());
			booleans.put(BotBooleanComponent.ConvertColors, legacyConfig.get("display", "enableIRCColors", BotBooleanComponent.ConvertColors.defaultValue).getBoolean());
		}
	}
}
