package com.example.authserver.exception;

/**
 * Exception thrown when the request is invalid.
 */
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException() {
        super();
    }

    public InvalidRequestException(String message) {
        super(message);
    }

}
