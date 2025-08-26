package com.example.authserver.repository;

import com.example.authserver.entity.AuthorizationConsent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for managing {@link AuthorizationConsent} entities.
 *
 * @see AuthorizationConsent
 */
public interface AuthorizationConsentRepository
        extends JpaRepository<AuthorizationConsent,String> {


    List<AuthorizationConsent> findByUserId(String userId);

    /**
     * Finds an authorization consent by user ID and client ID.
     *
     * @param userId   the user identifier
     * @param clientId the client identifier
     * @return the matching {@link AuthorizationConsent}, or {@code null} if none found
     */
    AuthorizationConsent findByUserIdAndClientId(String userId,String clientId);


    void deleteAllByUserIdAndClientId(String userId,String clientId);
}
