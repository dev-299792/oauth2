package com.example.authserver.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * Custom {@link AbstractAuthenticationToken} implementation for PKCE (Proof Key for Code Exchange) authentication.
 *
 * Represents both the unauthenticated request (containing an authorization code and code verifier)
 * and the authenticated state (containing the validated clientId).
 *
 * Used by {@link PkceAuthenticationProvider}.
 */
public class PkceAutheticationToken extends AbstractAuthenticationToken {

    private String clientId;
    private String authorizationCode;
    private String codeVerifier;

    /**
     * Private constructor for creating an unauthenticated token.
     *
     * @param authorizationCode the authorization code issued to the client
     * @param codeVerifier      the code verifier provided by the client
     */
    private PkceAutheticationToken(String authorizationCode, String codeVerifier) {
        super(null);
        setAuthenticated(false);
        this.authorizationCode = authorizationCode;
        this.codeVerifier = codeVerifier;
    }

    /**
     * Creates an authenticated token after successful PKCE verification.
     *
     * @param clientId the client ID that has been verified
     */
    public PkceAutheticationToken(String clientId) {
        super(null);
        setAuthenticated(true);
        this.clientId = clientId;
    }

    /**
     * Factory method for creating an unauthenticated PKCE token.
     *
     * @param authorizationCode the authorization code issued to the client
     * @param codeVerifier      the code verifier provided by the client
     * @return an unauthenticated {@link PkceAutheticationToken}
     */
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
