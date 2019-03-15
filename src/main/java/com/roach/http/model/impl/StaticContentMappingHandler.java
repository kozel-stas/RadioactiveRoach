package com.roach.http.model.impl;

import com.roach.http.model.HttpRequest;
import com.roach.http.model.HttpResponse;
import com.roach.http.model.MappingHandler;
import com.roach.http.model.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class StaticContentMappingHandler implements MappingHandler {

    private String fileName; //vlad.txt
    private List <Method> methods = new ArrayList<>();
    private Pattern uri;

    public StaticContentMappingHandler (String fileName) {
        methods.add(Method.GET);
        this.fileName = fileName;
        uri = Pattern.compile(fileName);
    }

    @Override
    public Pattern getUri() {
        return uri;
    }

    @Override
    public List<Method> getSupportedMethods() {
        return methods;
    }

    @Override
    public boolean isAcceptable(HttpRequest httpRequest, HttpResponse httpResponse) {
        // заголовки из httpRequest httpHeaders . чекать предпочтительный тип файла
        return true;
    }

    @Override
    public void handleRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
        FileLoader fileLoader = new FileLoaderImpl(fileName);
    }
}
