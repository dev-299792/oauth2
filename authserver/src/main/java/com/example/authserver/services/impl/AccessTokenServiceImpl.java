package com.example.authserver.services.impl;

import com.example.authserver.dto.AccessTokenRequestDTO;
import com.example.authserver.dto.AccessTokenResponseDTO;
import com.example.authserver.entity.AccessToken;
import com.example.authserver.entity.AuthorizationCode;
import com.example.authserver.entity.Client;
import com.example.authserver.enums.GrantType;
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
 * Implementation of the {@link AccessTokenService}.
 * <p>
 * This service handles the generation, validation, and persistence of access tokens
 * for different OAuth2 grant types such as Authorization Code and Refresh Token.
 */
@Service
@AllArgsConstructor
public class AccessTokenServiceImpl implements AccessTokenService {

    private final AccessTokenRepository accessTokenRepository;
    private final AuthorizationCodeRepository authorizationCodeRepository;
    private final JwtService jwtService;

    /**
     * Generates an access token based on the grant type specified in the request.
     *
     * @param requestDTO The access token request containing grant type, code, etc.
     * @return An {@link AccessTokenResponseDTO} containing the generated access token details.
     * @throws RestInvalidRequestException if the grant type is not supported.
     */
    @Transactional
    public AccessTokenResponseDTO generateAccessToken(AccessTokenRequestDTO requestDTO) {

        GrantType grantType;
        try {
            grantType = GrantType.valueOf(requestDTO.getGrant_type().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new RestInvalidRequestException("unsupported_grant_type");
        }

        try {
            AccessToken token = switch (grantType) {
                case AUTHORIZATION_CODE -> generateForAuthorizationCode(requestDTO);
                case REFRESH_TOKEN -> generateForRefreshToken(requestDTO);
                //case CLIENT_CREDENTIALS ->  ToDo: implement client credentials
                default -> throw new RestInvalidRequestException("unsupported_grant_type");
            };
            return toResponseDTO(token);
        } catch (Exception e) {
            // Todo: create and replace with rest internal server exception.
            throw new RestInvalidRequestException("something went wrong.");
        }

    }

    /**
     * Creates a new {@link AccessToken} for a user and client with given scopes.
     *
     * @param userId The user ID.
     * @param client The client requesting the token.
     * @param scopes The requested scopes.
     * @return A persisted {@link AccessToken}.
     */
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

    /**
     * Generates an access token using the Authorization Code grant type.
     *
     * @param requestDTO The request containing the authorization code and other parameters.
     * @return A newly created {@link AccessToken}.
     * @throws RestInvalidRequestException if the authorization code is invalid or expired.
     */
    private AccessToken generateForAuthorizationCode(AccessTokenRequestDTO requestDTO) {
        AuthorizationCode authorizationCode =
                authorizationCodeRepository
                        .findById(requestDTO.getCode())
                        .orElseThrow(() ->
                                new RestInvalidRequestException("invalid_request"));

        try {
            Client client = null;
            if (authorizationCode != null) {
                client = authorizationCode.getClient();
            }
            validateAccessTokenRequest(requestDTO, authorizationCode, client);
            return createAccessToken(authorizationCode.getUser_id(), client, authorizationCode.getScopes());
        }
        finally {
            if(authorizationCode!=null) {
                authorizationCodeRepository.delete(authorizationCode);
            }
        }
    }

    /**
     * Generates an access token using the Refresh Token grant type.
     *
     * @param requestDTO The request containing the refresh token and scopes.
     * @return A newly created {@link AccessToken}.
     * @throws RestInvalidRequestException if the refresh token is invalid or expired.
     */
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

    /**
     * Validates a refresh token request.
     *
     * @param oldToken   The old access token associated with the refresh token.
     * @param requestDTO The incoming request.
     * @throws RestInvalidRequestException if validation fails.
     */
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

    /**
     * Validates an authorization code access token request.
     *
     * @param requestDTO       The access token request.
     * @param authorizationCode The authorization code entity.
     * @param client           The associated client.
     * @throws RestInvalidRequestException if validation fails.
     */
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
    }

    /**
     * Converts an {@link AccessToken} entity into a response DTO.
     *
     * @param token The access token entity.
     * @return A populated {@link AccessTokenResponseDTO}.
     */
    private AccessTokenResponseDTO toResponseDTO(AccessToken token) {
        Duration accessTokenExp = Duration.between(token.getCreatedAt(), token.getExpiresAt());
        Duration refreshTokenExp = Duration.between(token.getCreatedAt(), token.getRefreshTokenExpiresAt());

        return AccessTokenResponseDTO.builder()
                .access_token(token.getToken())
                .refresh_token(token.getRefreshToken())
                .scope(token.getScopes())
                .expires_in(accessTokenExp.getSeconds())
                .refresh_token_expires_in(refreshTokenExp.getSeconds())
                .token_type("Bearer") // ToDo: remove hardcode
                .build();
    }

    /**
     * Retrieves the authenticated client ID from the security context.
     *
     * @return The client ID of the authenticated client.
     */
    private String getClientIdOfAuthenticatedClientId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return  (String) auth.getPrincipal();
    }

    /**
     * Checks if PKCE (Proof Key for Code Exchange) was verified during authentication.
     *
     * @return true if PKCE was verified, false otherwise.
     */
    private boolean isPkceVerified() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth instanceof PkceAutheticationToken;
    }

}
