package com.roach.http.model.parser;

import com.roach.http.model.HttpHeaders;

import java.text.ParseException;

public class HeadersParser1v1 implements HeadersParser {

    @Override
    public HttpHeaders parseHeadersAccordingToVersion(String[] headers) throws ParseException {
        HttpHeaders httpHeaders = new HttpHeaders();
        for (String line : headers) {
            int index = line.indexOf(':');
            if (index <= 0) {
                throw new ParseException("Unable to parse headers.", index);
            }
            String name = line.substring(0, index);
            String[] values = line.substring(index + 1, line.length()).trim().replaceAll(";q=[10].[0-9]", "").split(",");
            for (String val : values) {
                httpHeaders.addHeader(name, val);
            }

        }
        return httpHeaders;
    }

}
