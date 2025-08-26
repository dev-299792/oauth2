package com.example.authserver.services.impl;

import com.example.authserver.dto.UserDTO;
import com.example.authserver.entity.User;
import com.example.authserver.exception.EmailAlreadyUsedException;
import com.example.authserver.exception.UserAlreadyExistsException;
import com.example.authserver.repository.UserRepository;
import com.example.authserver.services.MailService;
import com.example.authserver.services.RegistrationService;
import com.example.authserver.services.VerificationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service implementation for registering new users.
 * <p>
 * This service handles creating new {@link User} entities, encoding passwords,
 * persisting users to the database, and sending verification emails.
 * It ensures that usernames and emails are unique before registration.
 * </p>
 *
 * @see RegistrationService
 */
@Service
@AllArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final VerificationTokenService verificationTokenService;

    /**
     * Registers a new user with the provided {@link UserDTO}.
     * <p>
     * The registration workflow includes:
     * </p>
     * <ol>
     *     <li>Check for existing username; throws {@link UserAlreadyExistsException} if it already exists.</li>
     *     <li>Check for existing email; throws {@link EmailAlreadyUsedException} if it already exists.</li>
     *     <li>Create a new {@link User} entity with a randomly generated UUID and encoded password.</li>
     *     <li>Set the user as disabled until email verification is complete.</li>
     *     <li>Persist the user entity to the database using {@link UserRepository}.</li>
     *     <li>Generate a verification token via {@link VerificationTokenService} and send a verification email through {@link MailService}.</li>
     * </ol>
     *
     * @param userDTO the user registration data transfer object containing username, password, and email
     * @throws UserAlreadyExistsException if a user with the given username already exists
     * @throws EmailAlreadyUsedException if a user with the given email already exists
     */
    @Transactional
    public void registerUser(UserDTO userDTO) {

        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new UserAlreadyExistsException();
        }

        if(userRepository.existsByEmail(userDTO.getEmail())) {
            throw new EmailAlreadyUsedException();
        }

        User user = User.builder()
                .user_id(UUID.randomUUID().toString())
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .email(userDTO.getEmail())
                .enabled(false)
                .build();

        userRepository.save(user);
        String token = verificationTokenService.generateToken(user);
        mailService.sendVerificationMail(user.getEmail(),token);
    }

}
