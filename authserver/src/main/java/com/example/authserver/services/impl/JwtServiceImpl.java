package com.example.authserver.services.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example.authserver.services.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Map;

/**
 * Service implementation for generating and verifying JWT tokens using RSA256.
 *
 * <p>This service handles creation of signed JWTs with custom claims and expiration,
 * as well as verification of tokens to ensure integrity and authenticity.</p>
 *
 * <p>It uses an RSA key pair loaded from PEM files on the classpath.</p>
 */
@Service
public class JwtServiceImpl implements JwtService {

    private final Algorithm rsa256;
    private final JWTVerifier verifier;

    /**
     * Constructs a JwtServiceImpl with the provided RSA public and private keys.
     *
     * @param publicKey  the RSA public key used to verify JWT signatures
     * @param privateKey the RSA private key used to sign JWTs
     */
    public JwtServiceImpl(@Value("classpath:keystore/public.pem") final RSAPublicKey publicKey,
                          @Value("classpath:keystore/private.pem") final RSAPrivateKey privateKey) {
        this.rsa256 = Algorithm.RSA256(publicKey, privateKey);
        this.verifier = JWT.require(this.rsa256).build();
    }

    /**
     * Generates a signed JWT token.
     *
     * @param subject  the subject (usually the user ID or username) of the token
     * @param expiresAt the expiration date of the token
     * @param claims   a map of custom claims to include in the JWT
     * @return a signed JWT token as a string
     */
    public String generateToken(String subject, Date expiresAt, Map<String, String> claims) {
        var jwtBuilder = JWT.create()
                .withSubject(subject)
                .withIssuedAt(new Date())
                .withIssuer("https://secureLoginOauthProvider.com") // ToDo: change issuer
                .withExpiresAt(expiresAt);

        claims.forEach(jwtBuilder::withClaim);
        return jwtBuilder.sign(rsa256);
    }

    /**
     * Verifies a JWT token and returns the decoded JWT.
     *
     * @param token the JWT token to verify
     * @return the decoded JWT containing claims and metadata
     * @throws com.auth0.jwt.exceptions.JWTVerificationException if the token is invalid or tampered with
     */
    public DecodedJWT verifyToken(String token) {
        return verifier.verify(token);
    }

}
