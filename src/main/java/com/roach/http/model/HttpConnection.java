package com.roach.http.model;

import com.google.common.base.Preconditions;
import com.roach.utils.UniqueGeneratorUtils;

import java.nio.channels.SocketChannel;

public class HttpConnection {

    private final String id;
    private final SocketChannel channel;
    private final long creationTime;
    private volatile int inProcess;
    private volatile long keepAliveTimeout;
    private volatile boolean forceClose = false;

    public HttpConnection(SocketChannel channel, long creationTime, long keepAliveTimeout) {
        id = UniqueGeneratorUtils.nextUniqueID();
        this.channel = Preconditions.checkNotNull(channel);
        this.creationTime = creationTime;
        this.keepAliveTimeout = keepAliveTimeout;
        // must be changed to false after processing.
        inProcess = 0;
    }

    public synchronized void onStartProcessing() {
        inProcess++;
    }

    public synchronized void onFinishProcessing() {
        inProcess--;
        assert inProcess >= 0;
    }

    public void setKeepAliveTimeout(long keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }

    public boolean isTimedOut(long now) {
        return forceClose | (now > creationTime + keepAliveTimeout) && !(inProcess > 0);
    }

    public boolean isTimedOutIgnoreProcessing(long now) {
        return forceClose | (now > creationTime + keepAliveTimeout);
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public String getId() {
        return id;
    }

    public void enableForceClose() {
        this.forceClose = true;
    }

}
