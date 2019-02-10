package com.roach.http.model;

import java.util.List;
import java.util.regex.Pattern;

public interface MappingHandler {

    Pattern getUri();

    List<Method> getSupportedMethods();

    boolean isAcceptable(HttpRequest httpRequest, HttpResponse httpResponse);

    void handleRequest(HttpRequest httpRequest, HttpResponse httpResponse);

}
