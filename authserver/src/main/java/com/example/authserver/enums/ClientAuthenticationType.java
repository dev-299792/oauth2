package com.example.authserver.enums;

public enum ClientAuthenticationType {
    NONE, CLIENT_SECRET_BASIC;
    public String getCode() {
        return this.name().toLowerCase();
    }
}
