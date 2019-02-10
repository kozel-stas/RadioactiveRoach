package com.roach.http.model;

import com.roach.http.model.parser.HeadersParser;
import com.roach.http.model.parser.HeadersParser1v1;

public enum Version {

    HTTP1V1("HTTP/1.1", new HeadersParser1v1()),
    NOT_SUPPORTED("*", (headers) -> HttpHeaders.STUB),;

    private final String name;
    private final HeadersParser headersParser;


    Version(String name, HeadersParser headersParser) {
        this.name = name;
        this.headersParser = headersParser;
    }

    public String getName() {
        return name;
    }

    public HeadersParser getHeadersParser() {
        return headersParser;
    }

    public boolean isSupported() {
        return this != NOT_SUPPORTED;
    }

    public static Version find(String name) {
        for (Version curr : Version.values()) {
            if (curr.name.equals(name)) {
                return curr;
            }
        }
        return NOT_SUPPORTED;
    }

}
