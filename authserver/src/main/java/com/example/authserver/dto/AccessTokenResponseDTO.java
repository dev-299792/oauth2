package com.example.authserver.dto;

import lombok.*;


/**
 * This class represents the response DTO for access token.
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessTokenResponseDTO {
    private String access_token;
    private Long expires_in;
    private String refresh_token;
    private Long refresh_token_expires_in;
    private String scope;
    private String tokenType;
}
