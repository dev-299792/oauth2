package com.example.authserver.services;

import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.Map;

public interface JwtService {
    String generateToken(String subject, Date expiresAt, Map<String, String> claims);
    DecodedJWT verifyToken(String token);
}