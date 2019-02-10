package com.roach.http.model;

public class HttpResponse {

    private final HttpConnection httpConnection;
    private ResponseCode responseCode;

    public HttpResponse(HttpConnection httpConnection) {
        this.httpConnection = httpConnection;
    }

    public void setResponseCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }
}
