package com.example.authserver.services;

import com.example.authserver.dto.ClientAuthorizationRedirectParams;
import com.example.authserver.entity.AuthorizationCode;
import com.example.authserver.entity.Client;
import com.example.authserver.repository.AuthorizationCodeRepository;
import com.example.authserver.repository.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ClientAuthorizationService {

    private final ClientRepository clientRepository;
    private final AuthorizationCodeRepository codeRepository;

    public String getAuthorizationCode(ClientAuthorizationRedirectParams params) {

        Client client = clientRepository.findClientByClientId(params.getClient_id()).orElse(null);

        if (client==null) return null;

        AuthorizationCode code = AuthorizationCode
                .builder()
                .code(UUID.randomUUID().toString())
                .username(getAuthenticatedUsername())
                .scopes(params.getScope())
                .redirectUri(params.getRedirect_uri())
                .client(client)
                .build();

        code = codeRepository.save(code);

        return code.getCode();
    }

    private String getAuthenticatedUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        return user.getUsername();
    }

}
