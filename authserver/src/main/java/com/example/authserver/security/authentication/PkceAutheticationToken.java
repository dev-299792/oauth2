package com.example.authserver.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class PkceAutheticationToken extends AbstractAuthenticationToken {

    private String clientId;
    private String authorizationCode;
    private String codeVerifier;

    private PkceAutheticationToken(String authorizationCode, String codeVerifier) {
        super(null);
        setAuthenticated(false);
        this.authorizationCode = authorizationCode;
        this.codeVerifier = codeVerifier;
    }

    public PkceAutheticationToken(String clientId) {
        super(null);
        setAuthenticated(true);
        this.clientId = clientId;
    }

    public static PkceAutheticationToken unauthenticated(String authorizationCode, String codeVerifier) {
        return new PkceAutheticationToken(authorizationCode, codeVerifier);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return clientId;
    }
    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public String getCodeVerifier() {
        return codeVerifier;
    }

}
