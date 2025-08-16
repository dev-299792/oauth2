package com.example.authserver.services;

import com.example.authserver.entity.AuthorizationConsent;

import java.util.Set;

/**
 * Service for managing user authorization consents.
 */
public interface AuthorizationConsentService {

    /**
     * Checks if consent already exists for the given client and scope.
     *
     * @param clientId the client ID
     * @param scope the requested scope
     * @return true if consent already exists, false otherwise
     */
    boolean consentExistsForScope(String clientId, String scope);

    /**
     * Returns the set of new scopes requested by the client that the user has not yet consented to.
     *
     * @param clientId the client ID
     * @param scope the requested scopes
     * @return a set of new scopes
     */
    Set<String> getNewRequestedScopes(String clientId, String scope);

    /**
     * Saves the user's consent for the given client and scopes.
     *
     * @param clientId the client ID
     * @param scope the scopes to save
     * @return the saved AuthorizationConsent entity
     */
    AuthorizationConsent saveConsent(String clientId, String scope);
}
