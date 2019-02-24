package com.roach.http.model.parser;

import com.google.common.net.HttpHeaders;
import com.roach.http.model.*;
import com.roach.http.service.NioHttpServer;
import com.roach.utils.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class HttpMessageParser {

    public static final HttpMessageParser INSTANCE = new HttpMessageParser();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);

    private HttpMessageParser() {
        dateFormat.setTimeZone(TimeZone.getDefault());
        // non public
    }

    public HttpRequest createHttpRequestFromMessage(HttpMessage httpMessage) throws ParseException {
        String message = httpMessage.getMessage();
        String[] lines = message.split("\r\n");
        if (lines.length == 0) {
            throw new ParseException("Invalid request.", lines.length);
        }
        String[] methodUriVersion = lines[0].split(" ");
        if (methodUriVersion.length != 3) {
            throw new ParseException("Invalid request.", lines[0].length());
        }
        Method method = Method.find(methodUriVersion[0]);
        Version version = Version.find(methodUriVersion[2]);
        String uri = decode(methodUriVersion[1]);
        int lastHeader = 1;
        for (int i = 1; i < lines.length - 1; i++) {
            if (lines[i].trim().isEmpty() && lines[i + 1].trim().isEmpty()) {
                lastHeader = i;
                break;
            }
        }

        String[] body = message.split("\r\n\r\n");
        return new HttpRequest(
                httpMessage.getHttpConnection(),
                method,
                version,
                version.getHeadersParser().parseHeadersAccordingToVersion(StringUtils.concat(lines, 1, lastHeader)),
                uri,
                httpMessage.getStartProcessing(),
                body.length > 1 ? decode(body[1]) : null
        );
    }

    public HttpMessage createHttpMessageFromResponse(HttpRequest httpRequest, HttpResponse httpResponse) throws ParseException {
        StringBuilder out = new StringBuilder();
        out.append(getOrDefault(httpRequest.getVersion()).getName()).append(" ").append(httpResponse.getResponseCode().getCode()).append(" ").append(httpResponse.getResponseCode().getDetail()).append("\r\n");
        out.append(HttpHeaders.SERVER).append(": ").append(NioHttpServer.SERVER).append("\r\n");
        out.append(HttpHeaders.DATE).append(": ").append(dateFormat.format(Calendar.getInstance().getTime())).append("\r\n");
        out.append(HttpHeaders.CONNECTION).append(": ").append(getStateOfConnection(httpRequest, httpResponse)).append("\r\n");
        out.append(HttpHeaders.CONTENT_LENGTH).append(": ").append(0).append("\r\n").append("\r\n");
        return new HttpMessage(out.toString(), httpRequest.getHttpConnection(), 0);
    }

    private static Version getOrDefault(Version version) {
        return version.isSupported() ? version : Version.HTTP1V1;
    }

    private static String getStateOfConnection(HttpRequest httpRequest, HttpResponse response) {
        if (httpRequest.getHttpConnection().isTimedOutIgnoreProcessing(System.currentTimeMillis())) {
            return com.roach.http.model.HttpHeaders.CLOSE;
        } else {
            return com.roach.http.model.HttpHeaders.KEEP_ALIVE;
        }
    }

    private static String decode(String encoded) throws ParseException {
        try {
            return URLDecoder.decode(encoded, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ex) {
            throw new ParseException(ex.getMessage(), 0);
        }
    }

    private static String encode(String decoded) throws ParseException {
        try {
            return URLEncoder.encode(decoded, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ex) {
            throw new ParseException(ex.getMessage(), 0);
        }
    }

}
