package com.example.authserver.services;

import com.example.authserver.dto.ClientRegRequestDTO;
import com.example.authserver.dto.ClientRegResponseDTO;
import com.example.authserver.entity.Client;
import com.example.authserver.enums.ApplicationType;
import com.example.authserver.enums.ClientAuthenticationType;
import com.example.authserver.enums.GrantType;
import com.example.authserver.repository.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ClientRegistrationService {

    private final ClientRepository clientRepository;

    public List<Client> getAllClientsOfCurrentUser() {
        String username = getAuthenticatedUsername();
        if(username == null || username.isBlank() ) return null;

        return clientRepository.findClientByCreatedBy(username);
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
                .clientSecret(secret)
                .redirectUris(clientRegRequest.getRedirectUri())
                .createdBy(getAuthenticatedUsername())
                .build();

        client.setClientAuthenticationMethodsSet(clientAuth);
        client.setAuthorizationGrantTypesSet(grantType);

        client = clientRepository.save(client);

        return new ClientRegResponseDTO(client);
    }

    private String getAuthenticatedUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        return user.getUsername();
    }
}
