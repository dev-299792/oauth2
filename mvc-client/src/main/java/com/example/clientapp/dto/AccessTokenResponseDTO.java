package com.example.clientapp.dto;

import lombok.Data;

@Data
public class AccessTokenResponseDTO {
    private String access_token;
    private Long expires_in; //seconds
    private String refresh_token;
    private String scope;
    private String token_type;
    private String id_token;
}
