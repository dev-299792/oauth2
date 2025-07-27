package com.example.authserver.exception;

public class RedirectBackWithErrorException extends RuntimeException {

    public RedirectBackWithErrorException(String errorMessage) {
        super(errorMessage);
    }

}