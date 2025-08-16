package com.example.authserver.services;

import com.example.authserver.dto.AccessTokenRequestDTO;
import com.example.authserver.dto.AccessTokenResponseDTO;

/**
 * Service for generating access tokens.
 */
public interface AccessTokenService {

    /**
     * Generates an access token from the given request.
     *
     * @param requestDTO the access token request
     * @return the generated access token response
     */
    AccessTokenResponseDTO generateAccessToken(AccessTokenRequestDTO requestDTO);
}
