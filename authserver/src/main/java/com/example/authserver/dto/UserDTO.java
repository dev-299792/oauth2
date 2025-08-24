package com.example.authserver.dto;

import com.example.authserver.dto.validator.PasswordMatches;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


/**
 * DTO for user registration.
 *
 */
@Getter
@Setter
@PasswordMatches
public class UserDTO {

    @Size(min = 5, message = "Username must have at least 5 characters")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Username must contain only letters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Provide valid Email")
    private String email;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])(?=\\S+$).{8,}$",
            message = "Password must contain at least one digit, one uppercase letter, one lowercase letter, one special character, and be at least 8 characters long.")
    private String password;

    private String confirmPassword;
}
