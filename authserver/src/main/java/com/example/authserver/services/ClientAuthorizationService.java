package com.example.authserver.services;

import com.example.authserver.dto.ClientAuthorizationRedirectParams;
import com.example.authserver.entity.AuthorizationCode;
import com.example.authserver.entity.Client;
import com.example.authserver.entity.User;
import com.example.authserver.exception.InvalidRequestException;
import com.example.authserver.exception.RedirectBackWithErrorException;
import com.example.authserver.repository.AuthorizationCodeRepository;
import com.example.authserver.repository.ClientRepository;
import com.example.authserver.repository.UserRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ClientAuthorizationService {

    private final ClientRepository clientRepository;
    private final AuthorizationCodeRepository codeRepository;
    private final UserRepository userRepository;

    public String getAuthorizationCode(ClientAuthorizationRedirectParams params) {

        Client client = clientRepository.findClientByClientId(params.getClient_id()).orElse(null);

        validateClientDetails(client,params);

        AuthorizationCode code = AuthorizationCode
                .builder()
                .code(UUID.randomUUID().toString())
                .user_id(getAuthenticatedUser().getUser_id())
                .scopes(params.getScope())
                .redirectUri(params.getRedirect_uri())
                .client(client)
                .build();

        code = codeRepository.save(code);

        return code.getCode();
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }

    private void validateClientDetails(Client client, ClientAuthorizationRedirectParams params) {

        if (client==null){
            throw new InvalidRequestException("Unknown Client.");
        }

        if (!client.getRedirectUrisSet().contains(params.getRedirect_uri())) {
            throw new InvalidRequestException("Invalid Redirect Uri.");
        }

        if(!params.getResponse_type().equals("code")) {
            throw new RedirectBackWithErrorException("Invalid response_type.");
        }

        for(String scope : params.getScope().split(" ")) {
            if(scope.isBlank()) continue;
            if(!client.getScopesSet().contains(scope)) {
                throw new RedirectBackWithErrorException("Invalid scope.");
            }
        }
    }

    public void validateClientDetails(String clientId, ClientAuthorizationRedirectParams params) {
        Client client = clientRepository.findClientByClientId(clientId).orElse(null);
        validateClientDetails(client,params);
    }
}
