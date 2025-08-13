package com.example.authserver.enums;

public enum GrantType {
    AUTHORIZATION_CODE, CLIENT_CREDENTIALS, REFRESH_TOKEN;

    public String getCode() {
        return this.name().toLowerCase();
    }

    public static GrantType valueOfNullable(String name) {
        for (GrantType grantType : GrantType.values()) {
            if (grantType.name().equalsIgnoreCase(name)) {
                return grantType;
            }
        }
        return null;
    }
}
