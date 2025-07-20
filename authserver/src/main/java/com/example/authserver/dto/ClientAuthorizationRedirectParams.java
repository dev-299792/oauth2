package com.example.authserver.dto;

import lombok.Data;

@Data
public class ClientAuthorizationRedirectParams {
    String client_id;
    String response_type;
    String redirect_uri;
    String scope;
}
