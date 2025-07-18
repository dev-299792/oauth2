package com.example.authserver.services;

import com.example.authserver.dto.UserDTO;
import com.example.authserver.exception.UserAlreadyExistsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    public final JdbcUserDetailsManager userDetailsManager;
    public final PasswordEncoder passwordEncoder;

    public RegistrationService(JdbcUserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(UserDTO userDTO) {

        if(userDetailsManager.userExists(userDTO.getUsername())) {
            throw new UserAlreadyExistsException();
        }

        UserDetails user = User.withUsername(userDTO.getUsername())
                                .password(passwordEncoder.encode(userDTO.getPassword()))
                                .roles("USER")
                                .build();

        userDetailsManager.createUser(user);
    }

}


