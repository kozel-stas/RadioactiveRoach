package com.roach.http.model.impl;

import com.roach.http.model.Filter;
import com.roach.http.model.HttpHeaders;
import com.roach.http.model.HttpRequest;
import com.roach.http.model.HttpResponse;
import com.roach.http.model.ResponseCode;
import com.roach.http.model.exceptions.InvalidUserInputException;

import static com.roach.config.ConfigConstants.KEEP_ALIVE_TIME_OUT;
import static com.roach.config.ConfigConstants.REQUEST_TIMED_OUT;

public class SystemFilter implements Filter {

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
        if (HttpHeaders.KEEP_ALIVE.equalsIgnoreCase(value)) {
            httpRequest.getHttpConnection().setKeepAliveTimeout(KEEP_ALIVE_TIME_OUT);
        } else {
            httpRequest.getHttpConnection().enableForceClose();
        }
    }

}
