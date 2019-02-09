package com.roach.http.service;

import com.roach.http.model.HttpConnection;

public interface HttpConnectionManager {

    HttpConnection addConnection(HttpConnection pendingConnection);

    HttpConnection touchConnection(String id);

    HttpConnection removeConnection(String id);

    HttpConnection removeConnection(HttpConnection httpConnection);

}
