package com.roach.config;

import com.google.common.base.Preconditions;
import com.roach.http.service.NioHttpServer;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public enum ConfigFields {

    LISTEN_PORT("roach.port", String.valueOf(NioHttpServer.SOCKET_PORT), (value) -> {
        NioHttpServer.SOCKET_PORT = Integer.valueOf(value);
    }),
    LISTEN_ADDRESS("roach.address", NioHttpServer.SOCKET_ADDRESS, (value) -> {
        NioHttpServer.SOCKET_ADDRESS = value;
    }),
    BUFFER_SIZE("roach.buffer.size", String.valueOf(NioHttpServer.BUFFER_SIZE), (value) -> {
        NioHttpServer.BUFFER_SIZE = Integer.valueOf(value);
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
