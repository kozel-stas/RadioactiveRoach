package com.roach.http.service;

import com.google.common.base.Preconditions;
import com.roach.http.model.HttpConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HttpConnectionManagerImpl implements HttpConnectionManager, Runnable {

    private static final Logger LOG = LogManager.getLogger(HttpConnectionManagerImpl.class);

    private final ConcurrentHashMap<String, HttpConnection> connections = new ConcurrentHashMap<>();

    public HttpConnectionManagerImpl(ScheduledExecutorService executorService) {
        executorService.scheduleWithFixedDelay(this, 5, 1, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        for (HttpConnection httpConnection : connections.values()) {
            if (httpConnection.isTimedOut(System.currentTimeMillis())) {
                removeConnection(httpConnection);
            }
        }
    }

    @Override
    public HttpConnection addConnection(HttpConnection pendingConnection) {
        Preconditions.checkNotNull(pendingConnection);
        LOG.debug("Connection was added {}", pendingConnection);
        return connections.put(pendingConnection.getId(), pendingConnection);
    }

    @Override
    public HttpConnection touchConnection(String id) {
        Preconditions.checkNotNull(id);
        HttpConnection httpConnection = connections.get(id);
        if (httpConnection == null) {
            LOG.warn("HttpConnection already removed for key {}.", id);
        } else {
            if (httpConnection.isTimedOut(System.currentTimeMillis())) {
                LOG.debug("Connection was timed out, {}", id);
                removeConnection(id);
                return null;
            }
        }
        return httpConnection;
    }

    @Override
    public HttpConnection removeConnection(String id) {
        Preconditions.checkNotNull(id);
        HttpConnection httpConnection = connections.remove(id);
        if (httpConnection == null) {
            LOG.warn("HttpConnection already removed for key {}.", id);
        } else {
            try {
                httpConnection.getChannel().close();
            } catch (IOException e) {
                LOG.error("Exception during closing chanel.",e);
            }
            LOG.debug("Delete connection {}", httpConnection);
        }
        return httpConnection;
    }

    @Override
    public HttpConnection removeConnection(HttpConnection httpConnection) {
        return removeConnection(httpConnection.getId());
    }

}
