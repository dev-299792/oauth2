package com.example.authserver.exception;

/**
 * This exception is thrown to redirect back to the client with an error.
 * This exception is thrown in the case of an invalid request or when the client is not authorized.
 */
public class RedirectBackWithErrorException extends RuntimeException {

    public RedirectBackWithErrorException(String errorMessage) {
        super(errorMessage);
    }

}