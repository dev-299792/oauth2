package com.example.authserver.dto.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation to ensure that the password fields of a DTO match.
 * <p>
 * This annotation is applied at the class level (type) and is validated by
 * {@link PasswordMatchesValidator}. It is used for registration where
 * "password" and "confirmPassword" field must be identical.
 * </p>
 *
 */
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PasswordMatches {
    String message() default "Password do not match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
