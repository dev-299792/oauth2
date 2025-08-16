package com.example.authserver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * DTO for the parameters in the authorization request.
 *
 */
@Data
public class ClientAuthorizationRedirectParams {

    @NotNull(message = "is blank or missing")
    @NotBlank(message = "is blank or missing")
    String client_id;

    @NotNull(message = "is blank or missing")
    @NotBlank(message = "is blank or missing")
    String response_type;

    @NotNull(message = "is blank or missing")
    @NotBlank(message = "is blank or missing")
    String redirect_uri;

    @NotNull(message = "is blank or missing")
    @NotBlank(message = "is blank or missing")
    String scope;

    String state;

    String code_challenge;

    String code_challenge_method;
}
