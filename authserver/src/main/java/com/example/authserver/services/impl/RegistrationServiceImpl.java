package com.example.authserver.services.impl;

import com.example.authserver.dto.UserDTO;
import com.example.authserver.entity.User;
import com.example.authserver.exception.UserAlreadyExistsException;
import com.example.authserver.repository.UserRepository;
import com.example.authserver.services.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service implementation for registering new users.
 *
 * <p>This service handles creating new {@link User} entities, encoding passwords,
 * and persisting users to the database. It also validates that usernames are unique
 * before registration.</p>
 */
@Service
@AllArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user with the provided {@link UserDTO}.
     *
     * <p>This method performs the following steps:</p>
     * <ol>
     *     <li>Checks if a user with the given username already exists and throws {@link UserAlreadyExistsException} if true.</li>
     *     <li>Creates a new {@link User} entity with a randomly generated UUID.</li>
     *     <li>Encodes the user's password using the configured {@link PasswordEncoder}.</li>
     *     <li>Marks the user as enabled and saves the entity to the {@link UserRepository}.</li>
     * </ol>
     *
     * @param userDTO the user registration data transfer object containing username and password
     * @throws UserAlreadyExistsException if a user with the given username already exists
     */
    public void registerUser(UserDTO userDTO) {

        if (userRepository.existsByUsername(userDTO.getUsername())) {
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
