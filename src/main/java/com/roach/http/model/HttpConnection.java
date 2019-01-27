package com.roach.http;

import com.google.common.base.Preconditions;

import java.nio.channels.SocketChannel;

public class HttpConnection {

    private final SocketChannel channel;
    private final long creationTime;
    private volatile boolean inProcess;
    private volatile long keepAliveTimeout;

    public HttpConnection(SocketChannel channel, long creationTime, long keepAliveTimeout) {
        this.channel = Preconditions.checkNotNull(channel);
        this.creationTime = creationTime;
        this.keepAliveTimeout = keepAliveTimeout;
        // must be changed to false after processing.
        inProcess = true;
    }

    public void onFinishProcessing() {
        inProcess = false;
    }

    public void setKeepAliveTimeout(long keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }

    public boolean isTimedOut(long now) {
        return now > creationTime + keepAliveTimeout || !inProcess;
    }

    public SocketChannel getChannel() {
        return channel;
    }

}
