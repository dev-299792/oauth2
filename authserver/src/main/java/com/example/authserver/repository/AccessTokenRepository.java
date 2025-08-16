package com.example.authserver.repository;

import com.example.authserver.entity.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for managing {@link AccessToken} entities.
 *
 * @see AccessToken
 */
public interface AccessTokenRepository extends JpaRepository<AccessToken,String> {

    /**
     * Finds an access token by its refresh token.
     *
     * @param refreshToken the refresh token value
     * @return an Optional containing the matching {@link AccessToken}, if found
     */
    Optional<AccessToken> findByRefreshToken(String refreshToken);
}
