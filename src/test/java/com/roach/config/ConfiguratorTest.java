package com.roach.config;

import org.junit.Assert;
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

    @Test
    public void testConfigurator() {
        configurator.setResourceName("config-test.roach");

        configurator.onStartup();

        Assert.assertEquals("127.0.0.2", ConfigConstants.SERVER_SOCKET_ADDRESS);
        Assert.assertEquals(2048, ConfigConstants.SERVER_BUFFER_SIZE);
        Assert.assertEquals(9090, ConfigConstants.SERVER_SOCKET_PORT);
    }


}
