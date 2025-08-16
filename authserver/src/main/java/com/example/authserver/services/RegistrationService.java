package com.example.authserver.services;

import com.example.authserver.dto.UserDTO;

public interface RegistrationService {
    void registerUser(UserDTO userDTO);
}