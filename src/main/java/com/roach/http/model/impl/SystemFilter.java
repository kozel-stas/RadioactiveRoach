package com.roach.http.model.impl;

import com.roach.http.model.*;
import com.roach.http.model.exceptions.InvalidUserInputException;

public class SystemFilter implements Filter {

    private static final long KEEP_ALIVE_TIME_OUT = 30_000;
    private static final long REQUEST_TIMED_OUT = 30_000;

    @Override
    public void processData(HttpRequest httpRequest, HttpResponse httpResponse) throws InvalidUserInputException {
        if (!httpRequest.getMethod().isSupported()) {
            httpResponse.setResponseCode(ResponseCode.NOT_IMPLEMENTED);
            httpRequest.getHttpConnection().enableForceClose();
            throw new InvalidUserInputException("Method is unsupported");
        }
        if (!httpRequest.getVersion().isSupported()) {
            httpResponse.setResponseCode(ResponseCode.HTTP_VERSION_NOT_SUPPORTED);
            httpRequest.getHttpConnection().enableForceClose();
            throw new InvalidUserInputException("Version is unsupported");
        }
        if (httpRequest.isTimedOut(System.currentTimeMillis(), REQUEST_TIMED_OUT)) {
            httpResponse.setResponseCode(ResponseCode.REQUEST_TIMEOUT);
            httpRequest.getHttpConnection().enableForceClose();
            throw new InvalidUserInputException("Request was timed out.");
        }
        HttpHeaders httpHeaders = httpRequest.getHttpHeaders();
        String value = httpHeaders.firstValue(com.google.common.net.HttpHeaders.CONNECTION);
        if (HttpHeaders.KEEP_ALIVE.equals(value)) {
            httpRequest.getHttpConnection().setKeepAliveTimeout(KEEP_ALIVE_TIME_OUT);
        } else {
            httpRequest.getHttpConnection().enableForceClose();
        }
    }

}
