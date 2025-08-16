package com.example.authserver.security.config;

import com.example.authserver.security.authentication.PkceAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;

import java.util.List;


/**
 * Registers a custom {@link AuthenticationManager}
 * that delegates authentication to {@link PkceAuthenticationProvider}.
 */
@Configuration
public class PkceSecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(PkceAuthenticationProvider pkceAuthenticationProvider) {
        return new ProviderManager(
                List.of(
                        pkceAuthenticationProvider
                )
        );
    }
}
