package com.example.authserver.exception;

/**
 * Exception thrown when the client makes an invalid rest request to the server.
 *
 * This exception is different from {@link InvalidRequestException} in that
 * it is thrown when the client is making a REST API request, rather than
 * a request to the authorization server itself.
 *
 * @see InvalidRequestException
 */
public class RestInvalidRequestException extends RuntimeException {
    public RestInvalidRequestException(String message) {
        super(message);
    }
}
