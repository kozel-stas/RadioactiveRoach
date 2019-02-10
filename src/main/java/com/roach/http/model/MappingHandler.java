package com.roach.http.model;

import java.util.List;
import java.util.regex.Pattern;

public interface MappingHandler {

    Pattern getUri();

    List<Method> getSupportedMethods();

    List<String> getReturnedTypes();

    void handleRequest(HttpRequest httpRequest, HttpResponse httpResponse);

}
