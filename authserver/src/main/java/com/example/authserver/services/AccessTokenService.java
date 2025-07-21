package com.example.authserver.services;

import com.example.authserver.dto.AccessTokenRequestDTO;
import com.example.authserver.entity.AccessToken;
import com.example.authserver.entity.AuthorizationCode;
import com.example.authserver.entity.Client;
import com.example.authserver.enums.GrantType;
import com.example.authserver.repository.AccessTokenRepository;
import com.example.authserver.repository.AuthorizationCodeRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
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

        if(authorizationCode==null) return null;

        Client client = authorizationCode.getClient();

        if(client==null) return null;

        if(nullOrNotEqual(getClientIdOfAuthenticatedClientId(), client.getClientId()) ||
                nullOrNotEqual(requestDTO.getRedirect_uri(), requestDTO.getRedirect_uri()) ||
            !GrantType.AUTHORIZATION_CODE.getCode().equals(requestDTO.getGrant_type())
        ) {
            return null;
        }

        AccessToken token = AccessToken
                .builder()
                .token(UUID.randomUUID().toString())
                .username(authorizationCode.getUsername())
                .refreshToken(UUID.randomUUID().toString())
                .client(client)
                .scopes("default") // ToDo: handle scope
                .build();

        token = accessTokenRepository.save(token);
        authorizationCodeRepository.delete(authorizationCode);

        return token;
    }

    private boolean nullOrNotEqual(Object o1, Object o2) {
        return (o1 == null) || (o2 == null) || !Objects.equals(o1, o2);
    }

    private String getClientIdOfAuthenticatedClientId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return  (String) auth.getPrincipal();
    }

}
