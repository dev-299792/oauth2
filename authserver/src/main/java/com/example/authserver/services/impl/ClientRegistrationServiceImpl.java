package com.example.authserver.services.impl;

import com.example.authserver.dto.ClientRegRequestDTO;
import com.example.authserver.dto.ClientRegResponseDTO;
import com.example.authserver.entity.Client;
import com.example.authserver.entity.User;
import com.example.authserver.enums.ApplicationType;
import com.example.authserver.enums.ClientAuthenticationType;
import com.example.authserver.enums.GrantType;
import com.example.authserver.repository.ClientRepository;
import com.example.authserver.services.ClientRegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service implementation for client registration and management.
 *
 * This service allows users to register new clients, retrieve existing clients,
 * and fetch all clients created by the currently authenticated user.
 * It also provides utility methods to convert client registration responses
 * into a map representation.
 */
@Service
@AllArgsConstructor
public class ClientRegistrationServiceImpl implements ClientRegistrationService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder encoder;

    /**
     * Retrieves a client by its clientId.
     *
     * @param clientId the unique identifier of the client
     * @return an Optional containing the client if found, or empty if not found
     */
    public Optional<Client> getClient(String clientId) {
        return clientRepository.findClientByClientId(clientId);
    }

    /**
     * Retrieves all clients created by the currently authenticated user.
     *
     * @return a list of ClientRegResponseDTO representing the user's clients
     */
    public List<ClientRegResponseDTO> getAllClientsOfCurrentUser() {
        User user = getAuthenticatedUser();
        return clientRepository
                .findClientByCreatedBy(user.getUser_id())
                .stream()
                .map(this::getClientRegResponseDTO)
                .toList();
    }

    /**
     * Registers a new client with the provided registration request.
     *
     * @param clientRegRequest the client registration request containing
     *                         client name, redirect URI, and application type
     * @return a ClientRegResponseDTO containing details of the registered client,
     *         including the encoded client secret
     */
    public ClientRegResponseDTO registerClient(ClientRegRequestDTO clientRegRequest) {

        String clientId = UUID.randomUUID().toString();
        String secret = UUID.randomUUID().toString();

        Set<String> clientAuth = new HashSet<>();
        Set<String> grantType = new HashSet<>();
        grantType.add(GrantType.AUTHORIZATION_CODE.getCode());

        if(ApplicationType.WEB_APP.equals(clientRegRequest.getApplicationType())) {
            grantType.add(GrantType.CLIENT_CREDENTIALS.getCode());
            clientAuth.add(ClientAuthenticationType.CLIENT_SECRET_BASIC.getCode());
        }

        Client client = Client
                .builder()
                .clientId(clientId)
                .clientName(clientRegRequest.getClientName())
                .clientSecret(encoder.encode(secret))
                .redirectUris(clientRegRequest.getRedirectUri())
                .scopes("read profile") // adding default scopes for now..
                .createdBy(getAuthenticatedUser().getUser_id())
                .build();

        client.setClientAuthenticationMethodsSet(clientAuth);
        client.setAuthorizationGrantTypesSet(grantType);

        client = clientRepository.save(client);

        ClientRegResponseDTO dto = getClientRegResponseDTO(client);
        dto.setClientSecret(client.getClientSecret());
        return dto;
    }

    /**
     * Converts a ClientRegResponseDTO into a map representation.
     *
     * @param dto the client registration response DTO
     * @return a map where the keys are descriptive labels and the values are the corresponding client details
     */
    public Map<String, String> convertClientRegResponseToMap(ClientRegResponseDTO dto) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("Client Id", dto.getClientId());
        if(dto.getClientSecret() !=null && !dto.getClientSecret().isBlank()) {
            map.put("Client Secret", dto.getClientSecret());
        }
        map.put("Client Name", dto.getClientName());
        map.put("Redirect Uri", dto.getRedirectUri());
        map.put("Client Authentication Methods", dto.getClientAuthenticationMethods());
        map.put("Authorization Grant Types", dto.getAuthorizationGrantTypes());
        return map;
    }

    /**
     * Retrieves the currently authenticated user from the security context.
     *
     * @return the authenticated User
     */
    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return  (User) auth.getPrincipal();
    }

    /**
     * Converts a Client entity to a ClientRegResponseDTO.
     *
     * @param client the client entity
     * @return a ClientRegResponseDTO representing the client
     */
    private ClientRegResponseDTO getClientRegResponseDTO(Client client) {
        return ClientRegResponseDTO.builder()
                .clientId(client.getClientId())
                .clientName(client.getClientName())
                .redirectUri(client.getRedirectUris())
                .clientAuthenticationMethods(client.getClientAuthenticationMethods())
                .authorizationGrantTypes(client.getAuthorizationGrantTypes())
                .build();
    }
}
