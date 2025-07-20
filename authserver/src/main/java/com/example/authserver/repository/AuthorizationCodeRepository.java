package com.example.authserver.repository;

import com.example.authserver.entity.AuthorizationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorizationCodeRepository extends JpaRepository<AuthorizationCode,String> {
}
