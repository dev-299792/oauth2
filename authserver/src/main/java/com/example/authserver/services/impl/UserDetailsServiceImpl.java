package com.example.authserver.services.impl;

import com.example.authserver.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of Spring Security's {@link UserDetailsService} interface.
 *
 * <p>This service is responsible for retrieving user details from the database
 * during authentication. It uses the {@link UserRepository} to fetch a user by
 * username and returns a {@link UserDetails} object required by Spring Security.</p>
 *
 * <p>If a user with the given username does not exist, a {@link UsernameNotFoundException}
 * is thrown.</p>
 */
@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads the user details for a given username.
     *
     * <p>This method queries the {@link UserRepository} for a user with the specified
     * username. If found, it returns the {@link UserDetails} implementation representing
     * that user. If not found, it throws {@link UsernameNotFoundException}.</p>
     *
     * @param username the username identifying the user whose data is required
     * @return the {@link UserDetails} for the given username
     * @throws UsernameNotFoundException if the user is not found in the repository
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }
}
