package com.example.authserver.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class BearerTokenAuthenticationToken extends AbstractAuthenticationToken {

    private final String token;
    private final Object principal;

    // Constructor for unauthenticated token (only raw token)
    public BearerTokenAuthenticationToken(String token) {
        super(null);
        this.token = token;
        this.principal = null;
        setAuthenticated(false);
    }

    // Constructor for authenticated token (with principal + authorities)
    public BearerTokenAuthenticationToken(Object principal, String token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public static BearerTokenAuthenticationToken unauthenticated(String token) {
        return new BearerTokenAuthenticationToken(token);
    }

    public static BearerTokenAuthenticationToken authenticated(Object principal, String token, Collection<? extends GrantedAuthority> authorities) {
        return new BearerTokenAuthenticationToken(principal, token, authorities);
    }
}
