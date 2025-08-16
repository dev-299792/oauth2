package com.example.authserver.dto;

import com.example.authserver.enums.ApplicationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;


/**
 * DTO for client registration request.
 */
@Getter
@Setter
public class ClientRegRequestDTO {

    @NotNull
    @NotBlank(message = "client name should not be blank")
    private String clientName;

    @NotNull(message = "you must select an Application Type")
    //@NotBlank(message = "Application type should not be blank")
    private ApplicationType applicationType;

    @NotNull
    @NotBlank(message = "Redirect uri should not be blank")
    @URL(message = "Invalid redirect uri")
    private String redirectUri;

}
