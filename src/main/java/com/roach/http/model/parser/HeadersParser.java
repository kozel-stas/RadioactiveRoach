package com.roach.http.model.parser;

import com.roach.http.model.HttpHeaders;

import java.text.ParseException;

public interface HeadersParser {

    HttpHeaders parseHeadersAccordingToVersion(String[] headers) throws ParseException;

}
