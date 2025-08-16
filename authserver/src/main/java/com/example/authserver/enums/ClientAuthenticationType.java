package com.example.authserver.enums;

/**
 * Enumeration representing the client authentication method.
 *
 */
public enum ClientAuthenticationType {
    NONE, CLIENT_SECRET_BASIC;
    public String getCode() {
        return this.name().toLowerCase();
    }
}
