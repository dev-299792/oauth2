package com.example.authserver.enums;

public enum GrantType {
    AUTHORIZATION_CODE, CLIENT_CREDENTIALS, REFRESH_TOKEN;

    public String getCode() {
        return this.name().toLowerCase();
    }
}
