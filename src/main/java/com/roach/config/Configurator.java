package com.roach.config;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.Resources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Configurator {

    private static final Logger LOG = LogManager.getLogger(Configurator.class);

    private static final char SEPARATOR = '=';
    private String RESOURCE_NAME = "config.roach";

    public void onStartup() {
        LOG.info("Reading configs from {}", RESOURCE_NAME);
        List<String> configs = new ArrayList<>();
        try {
            configs = Resources.readLines(Resources.getResource(RESOURCE_NAME), Charset.defaultCharset());
        } catch (IOException e) {
            LOG.error("Exception was thrown, default settings was set.", e);
        }
        for (String config : configs) {
            int separator = config.indexOf(SEPARATOR);
            String configName = config.substring(0, separator == -1 ? 0 : separator);
            ConfigFields configFields = ConfigFields.fromValue(configName);
            if (configFields != null) {
                configFields.acceptConfig(config.substring(separator + 1, config.length()));
                LOG.info("Config [{}] was set.", config);
            } else {
                LOG.warn("Config for name [{}] isn't find.", config);
            }
        }
        LOG.info("Server was configured.");
    }

    @VisibleForTesting
    public void setResourceName(String resourceName) {
        RESOURCE_NAME = resourceName;
    }

}
