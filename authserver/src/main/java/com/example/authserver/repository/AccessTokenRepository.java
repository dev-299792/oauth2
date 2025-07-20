package com.example.authserver.repository;

import com.example.authserver.entity.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessTokenRepository extends JpaRepository<AccessToken,String> {
}
