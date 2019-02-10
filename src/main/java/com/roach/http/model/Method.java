package com.roach.http.model;

public enum Method {

    GET("GET"),
    POST("POST"),
    HEAD("HEAD"),
    NOT_SUPPORTED("*");

    private final String name;

    Method(String name) {
        this.name = name;
    }

    public boolean isSupported() {
        return this != NOT_SUPPORTED;
    }

    public String getName() {
        return name;
    }

    public static Method find(String name) {
        for (Method method : Method.values()) {
            if (method.name.equals(name)) {
                return method;
            }
        }
        return NOT_SUPPORTED;
    }

}
