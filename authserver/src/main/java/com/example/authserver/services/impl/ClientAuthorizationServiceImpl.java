package com.example.authserver.services.impl;

import com.example.authserver.dto.ClientAuthorizationRedirectParams;
import com.example.authserver.entity.AuthorizationCode;
import com.example.authserver.entity.Client;
import com.example.authserver.entity.User;
import com.example.authserver.exception.InvalidRequestException;
import com.example.authserver.exception.RedirectBackWithErrorException;
import com.example.authserver.repository.AuthorizationCodeRepository;
import com.example.authserver.repository.ClientRepository;
import com.example.authserver.services.ClientAuthorizationService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of the ClientAuthorizationService.
 * This class handles generation of authorization codes for clients and validates client requests
 * according to OAuth2 authorization code flow.
 */
@Service
@AllArgsConstructor
public class ClientAuthorizationServiceImpl implements ClientAuthorizationService {

    private final ClientRepository clientRepository;
    private final AuthorizationCodeRepository codeRepository;

    /**
     * Generates a new authorization code for a client based on the provided redirect parameters.
     *
     * @param params the redirect parameters containing client ID, redirect URI, scope, and PKCE details
     * @return the generated authorization code
     * @throws InvalidRequestException if client validation fails
     * @throws RedirectBackWithErrorException if response_type or scope is invalid
     */
    @Transactional
    public String getAuthorizationCode(ClientAuthorizationRedirectParams params) {

        Client client = clientRepository.findClientByClientId(params.getClient_id()).orElse(null);

        validateClientDetails(client, params);

        AuthorizationCode code = AuthorizationCode
                .builder()
                .code(UUID.randomUUID().toString())
                .user_id(getAuthenticatedUser().getUser_id())
                .scopes(params.getScope())
                .redirectUri(params.getRedirect_uri())
                .client(client)
                .build();

        if (params.getCode_challenge() != null && !params.getCode_challenge().isBlank()) {
            code.setCodeChallenge(params.getCode_challenge());
            code.setCodeChallengeMethod(params.getCode_challenge_method());
        }

        code = codeRepository.save(code);

        return code.getCode();
    }

    /**
     * Retrieves the currently authenticated user from the security context.
     *
     * @return the authenticated User entity
     */
    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }

    /**
     * Validates the details of a client against the provided redirect parameters.
     * Checks for client existence, valid redirect URI, response type, and requested scopes.
     *
     * @param client the Client entity retrieved from the repository
     * @param params the redirect parameters containing client request details
     * @throws InvalidRequestException if client does not exist or redirect URI is invalid
     * @throws RedirectBackWithErrorException if response_type or scope is invalid
     */
    private void validateClientDetails(Client client, ClientAuthorizationRedirectParams params) {

        if (client == null) {
            throw new InvalidRequestException("Unknown Client.");
        }

        if (!client.getRedirectUrisSet().contains(params.getRedirect_uri())) {
            throw new InvalidRequestException("Invalid Redirect Uri.");
        }

        if (!params.getResponse_type().equals("code")) {
            throw new RedirectBackWithErrorException("Invalid response_type.");
        }

        for (String scope : params.getScope().split(" ")) {
            if (scope.isBlank()) continue;
            if (!client.getScopesSet().contains(scope)) {
                throw new RedirectBackWithErrorException("Invalid scope.");
            }
        }
    }

    /**
     * Validates client details for a given client ID and redirect parameters.
     *
     * @param clientId the ID of the client
     * @param params the redirect parameters containing client request details
     */
    public void validateClientDetails(String clientId, ClientAuthorizationRedirectParams params) {
        Client client = clientRepository.findClientByClientId(clientId).orElse(null);
        validateClientDetails(client, params);
    }
}
