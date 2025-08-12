package com.example.authserver.exception;

public class RestInvalidRequestException extends RuntimeException {
    public RestInvalidRequestException(String message) {
        super(message);
    }
}
