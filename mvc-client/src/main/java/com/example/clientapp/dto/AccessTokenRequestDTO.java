package com.example.clientapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccessTokenRequestDTO {
    private String code;
    private String client_id;
    private String client_secret;
    private String grant_type;
    private String redirect_uri;
    private String scopes;
}
