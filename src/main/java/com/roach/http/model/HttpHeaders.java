package com.roach.http.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class HttpHeaders {

    public static final String KEEP_ALIVE = "Keep-Alive";
    public static final String CLOSE = "Close";

    static final HttpHeaders STUB = new HttpHeaders();

    private final TreeMap<String, List<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public String firstValue(String name) {
        List<String> l = headers.get(name);
        return l == null ? null : l.get(0);
    }

    public List<String> allValues(String name) {
        return headers.get(name);
    }

    public Map<String, List<String>> map() {
        return Collections.unmodifiableMap(headers);
    }

    public void addHeader(String name, String value) {
        headers.computeIfAbsent(name, k -> new ArrayList<>(1))
                .add(value);
    }

    public void clear() {
        headers.clear();
    }

}
