package com.roach.http.model.exceptions;

public class InvalidUserInputException extends RuntimeException {

    public InvalidUserInputException(String message) {
        super(message, null, true, false);
    }

    public InvalidUserInputException(String message, Throwable cause) {
        super(message, cause, true, false);
    }

}
