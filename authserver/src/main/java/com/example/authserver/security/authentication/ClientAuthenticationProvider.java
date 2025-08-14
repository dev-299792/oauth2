package com.example.authserver.security.authentication;

import com.example.authserver.entity.Client;
import com.example.authserver.repository.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class ClientAuthenticationProvider implements AuthenticationProvider {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String clientId = (String) authentication.getPrincipal();
        String clientSecret = (String) authentication.getCredentials();

        Client client = clientRepository.findClientByClientId(clientId)
                .orElseThrow(() -> new BadCredentialsException("Invalid client credentials"));

        if (passwordEncoder.matches(clientSecret,client.getClientSecret())) {
            return new UsernamePasswordAuthenticationToken(clientId, null,null);
        }

        throw new BadCredentialsException("Invalid client credentials");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}