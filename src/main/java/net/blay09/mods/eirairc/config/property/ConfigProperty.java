// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.config.property;

public class ConfigProperty<T> {

    private final ConfigManager manager;
    private final String name;
    private final String category;
    private final T defaultValue;
    private T value;

    public ConfigProperty(ConfigManager manager, String name, String category,  T defaultValue) {
        this.manager = manager;
        this.name = name;
        this.category = category;
        this.defaultValue = defaultValue;

        manager.registerProperty(this);
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public T getDefaultValue() {
        return defaultValue;
    }
}
