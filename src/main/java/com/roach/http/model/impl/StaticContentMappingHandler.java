package com.roach.http.model.impl;

import com.roach.http.model.HttpRequest;
import com.roach.http.model.HttpResponse;
import com.roach.http.model.MappingHandler;
import com.roach.http.model.Method;

import java.util.List;
import java.util.regex.Pattern;

public class StaticContentMappingHandler implements MappingHandler {
    @Override
    public Pattern getUri() {
        return null;
    }

    @Override
    public List<Method> getSupportedMethods() {
        return null;
    }

    @Override
    public boolean isAcceptable(HttpRequest httpRequest, HttpResponse httpResponse) {
        return false;
    }

    @Override
    public void handleRequest(HttpRequest httpRequest, HttpResponse httpResponse) {

    }
}
