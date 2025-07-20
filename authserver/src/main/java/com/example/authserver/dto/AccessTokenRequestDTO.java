package com.example.authserver.dto;

import lombok.Data;

@Data
public class AccessTokenRequestDTO {
    private String code;
    private String client_id;
    private String client_secret;
    private String grant_type;
    private String redirect_uri;
}
