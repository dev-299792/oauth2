package com.example.authserver.services.impl;

import com.example.authserver.entity.User;
import com.example.authserver.entity.VerificationToken;
import com.example.authserver.repository.UserRepository;
import com.example.authserver.repository.VerificationTokenRepository;
import com.example.authserver.services.VerificationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation of the {@link VerificationTokenService} interface.
 * <p>
 * This service is responsible for generating and verifying email verification tokens
 * for newly registered users. It ensures that tokens are unique, not expired, and
 * marks the user as enabled upon successful verification.
 * </p>
 */
@Service
@AllArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;

    /**
     * Generates a unique verification token for the given {@link User}.
     * <p>
     * The token is persisted in the {@link VerificationTokenRepository} and
     * returned as a string, which can then be sent to the user's email address.
     * </p>
     *
     * @param user the {@link User} entity for which the verification token is generated
     * @return a unique token string
     */
    @Override
    public String generateToken(User user) {

        VerificationToken token = VerificationToken.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .token(UUID.randomUUID().toString())
                .used(false)
                .build();

        verificationTokenRepository.save(token);
        return token.getToken();
    }

    /**
     * Verifies the given token string.
     * <p>
     * Checks that the token exists, has not been used, and is not expired.
     * If the token is valid, the associated user's account is enabled,
     * and the token is deleted from the repository.
     * </p>
     *
     * @param token the verification token string to validate
     * @return {@code true} if the token is valid and the user's account is enabled,
     *         {@code false} if the token is invalid, used, or expired
     */
    @Override
    public boolean verifyToken(String token) {

        VerificationToken verificationToken = null;
        try {
            verificationToken = verificationTokenRepository
                                .findByToken(token).orElse(null);

            if (verificationToken != null &&
                    !verificationToken.isUsed() &&
                    LocalDateTime.now().isBefore(verificationToken.getExpiresAt())) {

                User user = verificationToken.getUser();
                user.setEnabled(true);
                userRepository.save(user);
                return true;
            }
        } finally {
            if(verificationToken != null) {
                verificationTokenRepository.delete(verificationToken);
            }
        }
        return false;
    }
}
