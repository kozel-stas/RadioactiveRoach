package com.roach.http.service;

import com.roach.config.ConfigConstants;
import com.roach.http.model.HttpConnection;
import com.roach.utils.MockSocketChannel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HttpConnectionManagerImplTest {

    private HttpConnectionManagerImpl httpConnectionManager;

    @Mock
    private ScheduledThreadPoolExecutor executor;
    private SocketChannel socketChannel;

    @Before
    public void beforeUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        socketChannel = new MockSocketChannel();

        httpConnectionManager = new HttpConnectionManagerImpl(executor);
    }

    @Test
    public void testNewConnection() {
        HttpConnection httpConnection = new HttpConnection(socketChannel, System.currentTimeMillis(), 10_000);
        httpConnectionManager.addConnection(httpConnection);

        Assert.assertEquals(httpConnection, httpConnectionManager.touchConnection(httpConnection.getId()));
    }

    @Test
    public void testTimedOutConnection() {
        HttpConnection httpConnection = new HttpConnection(socketChannel, System.currentTimeMillis(), 0);
        httpConnectionManager.addConnection(httpConnection);

        Assert.assertNull(httpConnectionManager.touchConnection(httpConnection.getId()));
    }

    @Test
    public void testRemoveTaskConnection() {
        HttpConnection httpConnection = new HttpConnection(socketChannel, System.currentTimeMillis(), 0);
        httpConnectionManager.addConnection(httpConnection);

        httpConnectionManager.run();

        Assert.assertNull(httpConnectionManager.removeConnection(httpConnection));
    }

    @Test
    public void testInitialRemoveTaskConnection() {
        Mockito.verify(executor, Mockito.timeout(1)).scheduleWithFixedDelay(Mockito.any(Runnable.class), Mockito.anyInt(), Mockito.eq(ConfigConstants.DISCONNECT_DELAY_IN_SECONDS), Mockito.eq(TimeUnit.SECONDS));
    }

    @Test
    public void testShutdown(){
        HttpConnection httpConnection = new HttpConnection(socketChannel, System.currentTimeMillis(), 50_000);
        httpConnectionManager.addConnection(httpConnection);

        httpConnectionManager.shutdown();

        Assert.assertNull(httpConnectionManager.removeConnection(httpConnection));
    }

    @Test
    public void testTimedOutConnectionButInProcess(){
        HttpConnection httpConnection = new HttpConnection(socketChannel, System.currentTimeMillis(), 50_000);
        httpConnection.onStartProcessing();
        httpConnectionManager.addConnection(httpConnection);

        httpConnectionManager.run();

        Assert.assertEquals(httpConnection, httpConnectionManager.touchConnection(httpConnection.getId()));
    }

}
