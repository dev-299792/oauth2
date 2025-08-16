package com.example.authserver.services;

import com.example.authserver.dto.ClientAuthorizationRedirectParams;

/**
 * Service for handling client authorization requests.
 */
public interface ClientAuthorizationService {

    /**
     * Generates an authorization code for the given client authorization parameters.
     *
     * @param params the client authorization parameters
     * @return the generated authorization code
     */
    String getAuthorizationCode(ClientAuthorizationRedirectParams params);

    /**
     * Validates the client details and request parameters.
     *
     * @param clientId the client ID
     * @param params the client authorization parameters
     */
    void validateClientDetails(String clientId, ClientAuthorizationRedirectParams params);
}
