package com.example.authserver.dto;

import lombok.Getter;
import lombok.Setter;


/**
 * DTO for user registration.
 *
 */
@Getter
@Setter
public class UserDTO {

    private String username;
    private String password;
}
