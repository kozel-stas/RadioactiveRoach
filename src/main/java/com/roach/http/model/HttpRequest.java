package com.roach.http.model;

import javax.annotation.Nullable;

public class HttpRequest {

    private final HttpConnection httpConnection;
    private final Method method;
    private final Version version;
    private final HttpHeaders httpHeaders;
    private final String uri;
    private final long startProcessingTime;
    @Nullable
    private final String body;

    public HttpRequest(HttpConnection httpConnection, Method method, Version version, HttpHeaders httpHeaders, String uri, long startProcessingTime, @Nullable String body) {
        this.httpConnection = httpConnection;
        this.method = method;
        this.version = version;
        this.httpHeaders = httpHeaders;
        this.uri = uri;
        this.startProcessingTime = startProcessingTime;
        this.body = body;
    }

    public HttpConnection getHttpConnection() {
        return httpConnection;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public Method getMethod() {
        return method;
    }

    @Nullable
    public String getBody() {
        return body;
    }

    public Version getVersion() {
        return version;
    }

    public String getUri() {
        return uri;
    }

    public boolean isTimedOut(long time, long timeOut) {
        return time - startProcessingTime > timeOut;
    }

}
