package com.example.authserver.repository;

import com.example.authserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for managing {@link User} entities.
 *
 * @see User
 */
public interface UserRepository extends JpaRepository<User,String> {

    /**
     * Finds a user by username.
     *
     * @param username the username to search for
     * @return an Optional containing the matching {@link User}, if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if a user with the given username exists.
     *
     * @param username the username to check
     * @return true if a user with the given username exists, false otherwise
     */
    boolean existsByUsername(String username);
}