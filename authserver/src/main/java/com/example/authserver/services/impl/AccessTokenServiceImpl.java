package com.example.authserver.services.impl;

import com.example.authserver.dto.AccessTokenRequestDTO;
import com.example.authserver.dto.AccessTokenResponseDTO;
import com.example.authserver.entity.AccessToken;
import com.example.authserver.entity.AuthorizationCode;
import com.example.authserver.entity.Client;
import com.example.authserver.enums.GrantType;
import com.example.authserver.exception.InvalidRequestException;
import com.example.authserver.exception.RestInvalidRequestException;
import com.example.authserver.repository.AccessTokenRepository;
import com.example.authserver.repository.AuthorizationCodeRepository;
import com.example.authserver.security.authentication.PkceAutheticationToken;
import com.example.authserver.services.AccessTokenService;
import com.example.authserver.services.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Implementation of the AccessTokenService.
 * This class is responsible for generating access tokens based on different grant types.
 */
@Service
@AllArgsConstructor
public class AccessTokenServiceImpl implements AccessTokenService {

    private final AccessTokenRepository accessTokenRepository;
    private final AuthorizationCodeRepository authorizationCodeRepository;
    private final JwtService jwtService;


    @Transactional
    public AccessTokenResponseDTO generateAccessToken(AccessTokenRequestDTO requestDTO) {

        GrantType grantType;
        try {
            grantType = GrantType.valueOf(requestDTO.getGrant_type().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new InvalidRequestException("unsupported_grant_type");
        }

        AccessToken token = switch (grantType) {
            case AUTHORIZATION_CODE -> generateForAuthorizationCode(requestDTO);
            case REFRESH_TOKEN -> generateForRefreshToken(requestDTO);
            //case CLIENT_CREDENTIALS ->  ToDo: implement client credentials
            default -> throw new InvalidRequestException("unsupported_grant_type");
        };
        return toResponseDTO(token);
    }

    private AccessToken createAccessToken(String userId, Client client, String scopes) {

        // 5 minutes from now
        Date expiresAt = new Date( new Date().getTime() + 5 * 60 * 1000 );

        var claims = Map.of(
                "client_id",client.getClientId(),
                "scopes",scopes
        );
        String jwtToken = jwtService.generateToken(userId, expiresAt, claims);

        AccessToken token = AccessToken
                .builder()
                .token(jwtToken)
                .user_id(userId)
                .refreshToken(UUID.randomUUID().toString())
                .client(client)
                .scopes(scopes)
                .build();
        return accessTokenRepository.save(token);
    }

    private AccessToken generateForAuthorizationCode(AccessTokenRequestDTO requestDTO) {
        AuthorizationCode authorizationCode =
                authorizationCodeRepository
                        .findById(requestDTO.getCode())
                        .orElseThrow(() ->
                                new InvalidRequestException("invalid_request"));

        try {
            Client client = null;
            if (authorizationCode != null) {
                client = authorizationCode.getClient();
            }
            validateAccessTokenRequest(requestDTO, authorizationCode, client);
            return createAccessToken(authorizationCode.getUser_id(), client, requestDTO.getScopes());
        }
        finally {
            if(authorizationCode!=null) {
                authorizationCodeRepository.delete(authorizationCode);
            }
        }
    }

    private AccessToken generateForRefreshToken(AccessTokenRequestDTO requestDTO) {
        AccessToken oldToken = accessTokenRepository.findByRefreshToken(requestDTO.getRefresh_token())
                .orElseThrow(() -> new RestInvalidRequestException("invalid_refresh_token"));

        try {
            validateRefreshTokenRequest(oldToken,requestDTO);
            return createAccessToken(oldToken.getUser_id(), oldToken.getClient(), requestDTO.getScopes());

        } finally {
            oldToken.setRefreshTokenExpiresAt(LocalDateTime.now().minusHours(1));
            accessTokenRepository.save(oldToken);
        }
    }

    private void validateRefreshTokenRequest(AccessToken oldToken, AccessTokenRequestDTO requestDTO) {

        String clientId = getClientIdOfAuthenticatedClientId();
        Client client = oldToken.getClient();
        if(client==null || !client.getClientId().equals(clientId)) {
            throw new RestInvalidRequestException("invalid_request");
        }

        if(oldToken.getRefreshTokenExpiresAt().isBefore(LocalDateTime.now()) ) {
            throw new RestInvalidRequestException("refresh_token_expired");
        }

        if(requestDTO.getScopes() == null || requestDTO.getScopes().isBlank()) {
            throw new RestInvalidRequestException("invalid_scope");
        }

        String[] requestedScopes = requestDTO.getScopes().split(" ");
        Set<String> oldTokenScopes = Set.of(oldToken.getScopes().split(" "));
        for(String scope : requestedScopes) {
            if(scope.isBlank()) continue;
            if(!oldTokenScopes.contains(scope)) {
                throw new RestInvalidRequestException("invalid_scope");
            }
        }
    }

    private void validateAccessTokenRequest(AccessTokenRequestDTO requestDTO, AuthorizationCode authorizationCode, Client client) {
        String clientId = getClientIdOfAuthenticatedClientId();
        if(client==null || !client.getClientId().equals(clientId)) {
            throw new RestInvalidRequestException("invalid_request");
        }

        if(authorizationCode.getCodeChallenge() != null &&
                !authorizationCode.getCodeChallenge().isBlank() &&
                !isPkceVerified()) {
            throw new RestInvalidRequestException("invalid_request");
        }

        if(authorizationCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RestInvalidRequestException("code_expired");
        }

        if(requestDTO.getRedirect_uri() == null ||
                !requestDTO.getRedirect_uri().equals(client.getRedirectUris()) ||
                !requestDTO.getRedirect_uri().equals(authorizationCode.getRedirectUri())
        ) {
            throw new RestInvalidRequestException("invalid_redirect_uri");
        }

        if(requestDTO.getScopes()==null || requestDTO.getScopes().isBlank()) {
            throw new InvalidRequestException("scope_absent");
        }

        String[] requestedScopes = requestDTO.getScopes().split(" ");
        Set<String> authCodeScopes = authorizationCode.getScopesSet();
        Set<String> clientScopes = client.getScopesSet();
        for(String scope : requestedScopes) {
            if(scope.isBlank()) continue;
            if(!authCodeScopes.contains(scope) || !clientScopes.contains(scope)) {
                throw new InvalidRequestException("invalid_scope");
            }
        }
    }

    private AccessTokenResponseDTO toResponseDTO(AccessToken token) {
        Duration accessTokenExp = Duration.between(token.getCreatedAt(), token.getExpiresAt());
        Duration refreshTokenExp = Duration.between(token.getCreatedAt(), token.getRefreshTokenExpiresAt());

        return AccessTokenResponseDTO.builder()
                .access_token(token.getToken())
                .refresh_token(token.getRefreshToken())
                .scope(token.getScopes())
                .expires_in(accessTokenExp.getSeconds())
                .refresh_token_expires_in(refreshTokenExp.getSeconds())
                .tokenType("Bearer") // ToDo: remove hardcode
                .build();
    }

    private String getClientIdOfAuthenticatedClientId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return  (String) auth.getPrincipal();
    }

    private boolean isPkceVerified() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth instanceof PkceAutheticationToken;
    }

}
