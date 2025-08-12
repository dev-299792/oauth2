package com.example.authserver.services;

import com.example.authserver.dto.UserDTO;
import com.example.authserver.entity.User;
import com.example.authserver.exception.UserAlreadyExistsException;
import com.example.authserver.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class RegistrationService {

    public final UserRepository userRepository;
    public final PasswordEncoder passwordEncoder;

    public void registerUser(UserDTO userDTO) {

        if(userRepository.existsByUsername(userDTO.getUsername())) {
            throw new UserAlreadyExistsException();
        }

        User user = User.builder()
                .user_id(UUID.randomUUID().toString())
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .email(userDTO.getUsername() + "@email.com")
                .enabled(true)
                .build();

        userRepository.save(user);
    }
}


