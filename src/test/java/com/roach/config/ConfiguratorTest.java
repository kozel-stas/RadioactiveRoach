package com.roach.config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConfiguratorTest {

    private Configurator configurator;

    @Before
    public void setUp() {
        configurator = new Configurator();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoFile() {
        configurator.setResourceName("config-ok");
        configurator.onStartup();
    }

}
