package com.example.authserver.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO for the client registration response.
 *
 */
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
