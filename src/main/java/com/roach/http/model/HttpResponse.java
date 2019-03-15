package com.roach.http.model;

import com.roach.http.model.impl.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HttpResponse {

    private final HttpConnection httpConnection;
    private ResponseCode responseCode;
    private ResponseBody responceBody;

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
