package com.example.authserver.services;

import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.Map;

/**
 * Service for generating and verifying JWT tokens.
 */
public interface JwtService {

    /**
     * Generates a JWT token with the given subject, expiration, and claims.
     *
     * @param subject   the subject (typically user ID)
     * @param expiresAt the token expiration date
     * @param claims    additional claims to include
     * @return the generated JWT as a string
     */
    String generateToken(String subject, Date expiresAt, Map<String, String> claims);

    /**
     * Verifies a JWT token and returns its decoded form.
     *
     * @param token the JWT token
     * @return the decoded JWT
     */
    DecodedJWT verifyToken(String token);
}
