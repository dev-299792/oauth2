package com.example.authserver.services;

import com.example.authserver.dto.ClientAuthorizationRedirectParams;

public interface ClientAuthorizationService {
    String getAuthorizationCode(ClientAuthorizationRedirectParams params);
    void validateClientDetails(String clientId, ClientAuthorizationRedirectParams params);
}