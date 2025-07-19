package com.example.authserver.dto;

import com.example.authserver.entity.Client;
import lombok.Data;

@Data
public class ClientRegResponseDTO {

    private String clientId;
    private String clientSecret;
    private String clientName;
    private String redirectUri;
    private String clientAuthenticationMethods;
    private String authorizationGrantTypes;

    public ClientRegResponseDTO(Client client) {
        clientId = client.getClientId();
        clientSecret = client.getClientSecret();
        clientName = client.getClientName();
        redirectUri = client.getRedirectUris();
        clientAuthenticationMethods = client.getClientAuthenticationMethods();
        authorizationGrantTypes = client.getAuthorizationGrantTypes();
    }
}
