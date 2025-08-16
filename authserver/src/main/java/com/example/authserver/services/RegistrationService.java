package com.example.authserver.services;

import com.example.authserver.dto.UserDTO;

/**
 * Service for registering new users.
 */
public interface RegistrationService {

    /**
     * Registers a new user based on the provided user data.
     *
     * @param userDTO the user data transfer object
     */
    void registerUser(UserDTO userDTO);
}
