package com.example.authserver.repository;

import com.example.authserver.entity.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccessTokenRepository extends JpaRepository<AccessToken,String> {
    Optional<AccessToken> findByRefreshToken(String refreshToken);
}
