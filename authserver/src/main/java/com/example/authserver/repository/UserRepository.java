package com.example.authserver.repository;

import com.example.authserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {
    public Optional<User> findByUsername(String username);
    public boolean existsByUsername(String username);
}