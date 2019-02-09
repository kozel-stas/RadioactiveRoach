package com.roach.http.service;

import com.roach.http.model.HttpMessage;

public interface HttpMultiProcessor {

    boolean offerData(HttpMessage httpMessage);

    void startProcessing();

    void shutdown();

}
