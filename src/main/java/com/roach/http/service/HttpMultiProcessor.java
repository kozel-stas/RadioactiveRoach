package com.roach.http.service;

import com.roach.http.model.HttpMessage;
import com.roach.http.model.MappingHandler;

public interface HttpMultiProcessor {

    boolean offerData(HttpMessage httpMessage);

    void startProcessing();

    void shutdown();

    void addHandler(MappingHandler mappingHandler);
}
