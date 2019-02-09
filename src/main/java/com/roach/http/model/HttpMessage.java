package com.roach.http.model;

public class HttpMessage {

    private final String message;
    private final HttpConnection httpConnection;
    private final long startProcessing;

    public HttpMessage(String message, HttpConnection httpConnection, long startProcessing) {
        this.message = message;
        this.httpConnection = httpConnection;
        this.startProcessing = startProcessing;
    }

    public HttpConnection getHttpConnection() {
        return httpConnection;
    }

    public String getMessage() {
        return message;
    }

    public void onStartProcessing() {
        httpConnection.onStartProcessing();
    }

    public void onFinishProcessing() {
        httpConnection.onFinishProcessing();
    }

    public long getStartProcessing() {
        return startProcessing;
    }

    @Override
    public String toString() {
        return "HttpMessage={message=" +
                message + ",connection=" +
                httpConnection + "}";
    }
}
