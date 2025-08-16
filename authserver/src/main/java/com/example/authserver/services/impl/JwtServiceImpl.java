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

@Service
public class JwtServiceImpl implements JwtService {

    private final Algorithm rsa256;
    private final JWTVerifier verifier;

    public JwtServiceImpl(@Value("classpath:keystore/public.pem") final RSAPublicKey publicKey,
                          @Value("classpath:keystore/private.pem") final RSAPrivateKey privateKey) {
        this.rsa256 = Algorithm.RSA256(publicKey, privateKey);
        this.verifier = JWT.require(this.rsa256).build();
    }

    public String generateToken(String subject, Date expiresAt, Map<String, String> claims) {
        var jwtBuilder = JWT.create()
                .withSubject(subject)
                .withIssuedAt(new Date())
                .withIssuer("https://secureLoginOauthProvider.com") // ToDo: change issuer
                .withExpiresAt(expiresAt);

        claims.forEach(jwtBuilder::withClaim);
        return jwtBuilder.sign(rsa256);
    }

    public DecodedJWT verifyToken(String token) {
        return verifier.verify(token);
    }

}