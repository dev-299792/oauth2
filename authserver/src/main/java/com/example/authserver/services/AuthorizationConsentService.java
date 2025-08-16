package com.example.authserver.services;

import com.example.authserver.entity.AuthorizationConsent;

import java.util.Set;

public interface AuthorizationConsentService {
    boolean consentExistsForScope(String clientId, String scope);
    Set<String> getNewRequestedScopes(String clientId, String scope);
    AuthorizationConsent saveConsent(String clientId, String scope);
}