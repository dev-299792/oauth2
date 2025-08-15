package com.example.authserver.security.authentication;

import com.example.authserver.entity.AuthorizationCode;
import com.example.authserver.repository.AuthorizationCodeRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PkceAuthenticationProvider implements AuthenticationProvider {

    private final AuthorizationCodeRepository authorizationCodeRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        PkceAutheticationToken token = (PkceAutheticationToken) authentication;
        AuthorizationCode code = authorizationCodeRepository.findById(token.getAuthorizationCode())
                .orElse(null);

        if (code != null && code.getCodeChallenge() != null) {
            String generatedChallenge = null;
            String method = code.getCodeChallengeMethod();
            if ("SHA256".equalsIgnoreCase(method)) {
                generatedChallenge = DigestUtils.sha256Hex(token.getCodeVerifier());
            } else if ("plain".equalsIgnoreCase(method)) {
                generatedChallenge = token.getCodeVerifier();
            }
            if (generatedChallenge != null && generatedChallenge.equals(code.getCodeChallenge())) {
                return new PkceAutheticationToken(code.getClient().getClientId());
            }
        }
        throw new BadCredentialsException("invalid_pkce_request.");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PkceAutheticationToken.class.isAssignableFrom(authentication);
    }
}
