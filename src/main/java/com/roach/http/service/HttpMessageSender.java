package com.roach.http.service;

import com.roach.http.model.HttpRequest;
import com.roach.http.model.HttpResponse;

public interface HttpMessageSender {

    boolean sendResponse(HttpRequest httpRequest, HttpResponse response);

}
