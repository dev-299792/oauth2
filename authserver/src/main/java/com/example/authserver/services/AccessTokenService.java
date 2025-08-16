package com.example.authserver.services;

import com.example.authserver.dto.AccessTokenRequestDTO;
import com.example.authserver.dto.AccessTokenResponseDTO;

public interface AccessTokenService {
    AccessTokenResponseDTO generateAccessToken(AccessTokenRequestDTO requestDTO);
}