package com.roach.http.model.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResponseBody {
    private List<String> headers = new ArrayList<>();

    public void composeResponseBody(String content, int code, String contentType, int contentLength) {
        headers.add("HTTP/1.1 " + code);
        headers.add("Server: Java http");
        headers.add("Date: " + new Date());
        headers.add("Content-type: " + contentType);
        headers.add("Content-length: " + contentLength);
    }
}
