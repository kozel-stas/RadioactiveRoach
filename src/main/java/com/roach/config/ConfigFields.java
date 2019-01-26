package com.roach.config;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public enum ConfigFields {

    LISTEN_PORT("roach.port", "8080", (value) -> {
        // TODO: introduce
    }),
    STATIC_CONTEXT_PATH("roach.static.content.path", "/", (value) -> {
        // TODO: introduce
    }),;

    private final String configName;
    private final String defaultConfig;
    private final Consumer<String> action;

    ConfigFields(String configName, String defaultConfig, Consumer<String> action) {
        this.configName = configName;
        this.defaultConfig = defaultConfig;
        this.action = action;
    }

    public void acceptConfig(@Nonnull String config) {
        Preconditions.checkNotNull(config);
        this.action.accept(config);
    }

    public String getDefaultConfig() {
        return defaultConfig;
    }

    public static ConfigFields fromValue(@Nonnull String name) {
        Preconditions.checkNotNull(name);
        for (ConfigFields config : ConfigFields.values()) {
            if (config.configName.equals(name.toLowerCase().trim())) {
                return config;
            }
        }
        return null;
    }

}
