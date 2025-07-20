package com.example.authserver.authentication;

import com.example.authserver.entity.Client;
import com.example.authserver.repository.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class ClientAuthenticationProvider implements AuthenticationProvider {

    private final ClientRepository clientRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String clientId = (String) authentication.getPrincipal();
        String clientSecret = (String) authentication.getCredentials();

        Client client = clientRepository.findClientByClientId(clientId)
                .orElseThrow(() -> new BadCredentialsException("Invalid client credentials"));

        if (clientSecret!=null && clientSecret.equals(client.getClientSecret())) {
            return new UsernamePasswordAuthenticationToken(clientId, null,null);
        }

        throw new BadCredentialsException("Invalid client credentials");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
