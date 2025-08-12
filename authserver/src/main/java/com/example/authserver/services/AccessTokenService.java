package com.example.authserver.services;

import com.example.authserver.dto.AccessTokenRequestDTO;
import com.example.authserver.entity.AccessToken;
import com.example.authserver.entity.AuthorizationCode;
import com.example.authserver.entity.Client;
import com.example.authserver.enums.GrantType;
import com.example.authserver.exception.InvalidRequestException;
import com.example.authserver.exception.RestInvalidRequestException;
import com.example.authserver.repository.AccessTokenRepository;
import com.example.authserver.repository.AuthorizationCodeRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AccessTokenService {

    private final AccessTokenRepository accessTokenRepository;
    private final AuthorizationCodeRepository authorizationCodeRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AccessToken generateAccessToken(AccessTokenRequestDTO requestDTO) {

        AuthorizationCode authorizationCode =
                authorizationCodeRepository
                        .findById(requestDTO.getCode())
                        .orElse(null);

        Client client=null;
        if(authorizationCode!=null) {
            client = authorizationCode.getClient();
        }

        validateAccessTokenRequest(requestDTO,authorizationCode,client);

        AccessToken token = AccessToken
                .builder()
                .token(UUID.randomUUID().toString())
                .user_id(authorizationCode.getUser_id())
                .refreshToken(UUID.randomUUID().toString())
                .client(client)
                .scopes("default") // ToDo: handle scope
                .build();

        token = accessTokenRepository.save(token);
        authorizationCodeRepository.delete(authorizationCode);

        return token;
    }

    private void validateAccessTokenRequest(AccessTokenRequestDTO requestDTO, AuthorizationCode authorizationCode, Client client) {
        String clientId = getClientIdOfAuthenticatedClientId();
        if(client==null || !client.getClientId().equals(clientId)) {
            throw new RestInvalidRequestException("invalid_request");
        }

        if(authorizationCode==null) {
            throw new RestInvalidRequestException("invalid_grant");
        }

        if(requestDTO.getRedirect_uri() == null ||
                !requestDTO.getRedirect_uri().equals(client.getRedirectUris()) ||
                !requestDTO.getRedirect_uri().equals(authorizationCode.getRedirectUri())
        ) {
            throw new RestInvalidRequestException("invalid_redirect_uri");
        }

        if(!GrantType.AUTHORIZATION_CODE.getCode().equals(requestDTO.getGrant_type())) {
            throw new InvalidRequestException("invalid_grant_type");
        }
    }

    private String getClientIdOfAuthenticatedClientId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return  (String) auth.getPrincipal();
    }
}
