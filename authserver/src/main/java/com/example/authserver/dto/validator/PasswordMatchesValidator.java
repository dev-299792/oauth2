package com.example.authserver.dto.validator;

import com.example.authserver.dto.UserDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for the {@link PasswordMatches} annotation.
 * <p>
 * Validates that the {@code password} and {@code confirmPassword} fields of a {@link UserDTO}
 * are both non-null and equal. This ensures that users enter matching passwords during registration
 * </p>
 */
public class PasswordMatchesValidator
        implements ConstraintValidator<PasswordMatches, UserDTO> {
    @Override
    public boolean isValid(UserDTO value, ConstraintValidatorContext context) {
        if(value.getPassword()==null || value.getConfirmPassword()==null) {
            return false;
        }
        return value.getPassword().equals(value.getConfirmPassword());
    }
}
