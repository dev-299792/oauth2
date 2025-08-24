package com.example.authserver.repository;

import com.example.authserver.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for managing {@link VerificationToken} entities.
 *
 * @see VerificationToken
 */
public interface VerificationTokenRepository extends JpaRepository<VerificationToken,String> {

    /**
     * Retrieves a {@link VerificationToken} entity by its token string.
     *
     * @param token the token string to search for
     * @return an {@link Optional} containing the {@link VerificationToken} if found,
     *         or empty if no token matches
     */
    Optional<VerificationToken> findByToken(String token);
}
