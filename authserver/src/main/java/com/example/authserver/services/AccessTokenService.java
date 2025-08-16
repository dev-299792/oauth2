package com.example.authserver.services;

import com.example.authserver.dto.AccessTokenRequestDTO;
import com.example.authserver.entity.AccessToken;

public interface AccessTokenService {
    AccessToken generateAccessToken(AccessTokenRequestDTO requestDTO);
}