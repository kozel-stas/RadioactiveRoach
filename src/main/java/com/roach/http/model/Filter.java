package com.roach.http.model;

import com.roach.http.model.exceptions.InvalidUserInputException;

public interface Filter {

    void processData(HttpRequest httpRequest, HttpResponse httpResponse) throws InvalidUserInputException;

}
