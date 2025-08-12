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
        if(client.getClientSecret()!=null && !client.getClientSecret().contains("{")) {
            clientSecret = client.getClientSecret();
        } else {
            clientSecret = null;
        }
        clientName = client.getClientName();
        redirectUri = client.getRedirectUris();
        clientAuthenticationMethods = client.getClientAuthenticationMethods();
        authorizationGrantTypes = client.getAuthorizationGrantTypes();
    }
}
