package com.example.authserver.security.authentication;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.authserver.entity.User;
import com.example.authserver.repository.UserRepository;
import com.example.authserver.services.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Configuration
@AllArgsConstructor
public class BearerTokenAuthenticationProvider implements AuthenticationProvider {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) {
        if (!(authentication instanceof BearerTokenAuthenticationToken bearerToken)) {
            return null;
        }

        try {
            String token = bearerToken.getToken();
            DecodedJWT jwt = jwtService.verifyToken(token);

            Instant expiresAt = jwt.getExpiresAtAsInstant();

            if(expiresAt == null || expiresAt.isBefore(Instant.now())) {
                throw new BadCredentialsException("invalid_token");
            }

            String userId = jwt.getSubject();
            String scopes = jwt.getClaim("scopes").asString();

            if(userId == null || scopes == null) {
                throw new BadCredentialsException("invalid_token");
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(()->new BadCredentialsException("invalid_token"));

            List<SimpleGrantedAuthority> authorities = Arrays.stream(scopes.split(" "))
                    .map(scope -> new SimpleGrantedAuthority("SCOPE_"+scope))
                    .toList();

            return BearerTokenAuthenticationToken.authenticated(user, token, authorities);

        } catch (JWTVerificationException e) {
            throw new BadCredentialsException("invalid_token");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return BearerTokenAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
