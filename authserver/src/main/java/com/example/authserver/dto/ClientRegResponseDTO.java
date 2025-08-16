package com.example.authserver.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientRegResponseDTO {
    private String clientId;
    private String clientSecret;
    private String clientName;
    private String redirectUri;
    private String clientAuthenticationMethods;
    private String authorizationGrantTypes;
}
