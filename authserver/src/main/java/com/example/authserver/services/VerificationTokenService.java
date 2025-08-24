package com.example.authserver.services;

import com.example.authserver.entity.User;

/**
 * Service interface for managing email verification tokens for users.
 * <p>
 * Provides functionality to generate tokens for newly registered users
 * and verify tokens when users click on verification links.
 * </p>
 */
public interface VerificationTokenService {

    /**
     * Generates a unique verification token for the given {@link User}.
     * <p>
     * This token is typically sent to the user's email address to verify their account.
     * </p>
     *
     * @param user the {@link User} entity for which the token is generated
     * @return a unique verification token string
     */
    String generateToken(User user);

    /**
     * Verifies whether a given token is valid and corresponds to a registered user.
     * <p>
     * Typically used when a user clicks on a verification link to activate their account.
     * </p>
     *
     * @param token the verification token to validate
     * @return {@code true} if the token is valid and the user's account can be verified,
     *         {@code false} if the token is invalid or expired
     */
    boolean verifyToken(String token);
}
