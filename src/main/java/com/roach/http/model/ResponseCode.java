package com.roach.http.model;

public enum ResponseCode {

    OK(200, "OK"),
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    MOVED_TEMPORARILY(302, "Moved Temporarily"),
    BAD_REQUEST(400, "Bad request"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not found"),
    NOT_ACCEPTABLE(406, "Not Acceptable"),
    REQUEST_TIMEOUT(408, "Request Timeout"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error."),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version NOT Supported"),;


    private final int code;
    private final String detail;

    ResponseCode(int code, String detail) {
        this.code = code;
        this.detail = detail;
    }

    public int getCode() {
        return code;
    }

    public String getDetail() {
        return detail;
    }

}
