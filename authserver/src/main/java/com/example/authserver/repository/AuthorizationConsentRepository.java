package com.example.authserver.repository;

import com.example.authserver.entity.AuthorizationConsent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorizationConsentRepository
        extends JpaRepository<AuthorizationConsent,String> {

    AuthorizationConsent findByUserIdAndClientId(String userId,String clientId);
}
