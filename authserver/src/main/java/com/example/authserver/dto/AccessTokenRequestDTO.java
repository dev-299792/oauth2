package com.example.authserver.dto;

import lombok.Data;

/**
 * A DTO for the access token request.
 */
@Data
public class AccessTokenRequestDTO {
    private String code;
    private String grant_type;
    private String redirect_uri;
    private String scopes;
    private String refresh_token;
}
