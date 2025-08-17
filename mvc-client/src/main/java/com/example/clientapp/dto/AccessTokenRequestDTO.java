package com.example.clientapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccessTokenRequestDTO {
    private String code;
    private String grant_type;
    private String redirect_uri;
}
