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

@Service
@AllArgsConstructor
public class ClientRegistrationServiceImpl implements ClientRegistrationService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder encoder;

    public Optional<Client> getClient(String clientId) {
        return clientRepository.findClientByClientId(clientId);
    }

    public List<ClientRegResponseDTO> getAllClientsOfCurrentUser() {
        User user = getAuthenticatedUser();
        return clientRepository
                .findClientByCreatedBy(user.getUser_id())
                .stream()
                .map(this::getClientRegResponseDTO)
                .toList();
    }

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

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return  (User) auth.getPrincipal();
    }

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
